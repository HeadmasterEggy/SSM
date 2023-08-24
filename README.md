# The IOC Container

## Introduction to the IOC Container and Beans

IoC 也称为依赖注入 (DI)。这是一个过程，对象仅通过构造函数参数、工厂方法的参数或在构造对象实例或从工厂方法返回后在对象实例上设置的属性来定义其依赖项（即它们使用的其他对象）。然后，容器在创建 bean 时注入这些依赖项。这个过程从根本上来说是 bean 本身的逆过程（因此得名“控制反转”），通过使用类的直接构造或诸如服务定位器模式之类的机制来控制其依赖项的实例化或位置。



`org.springframework.beans` 和 `org.springframework.context` 包是 Spring Framework 的 IoC 容器的基础。 `BeanFactory` 接口提供了能够管理任何类型对象的高级配置机制。 `ApplicationContext` 是 `BeanFactory` 的子接口。

- Easier integration with Spring’s AOP features
  更轻松地与 Spring 的 AOP 功能集成
- Message resource handling (for use in internationalization)
  消息资源处理（用于国际化）
- Event publication 活动发布
- Application-layer specific contexts such as the `WebApplicationContext` for use in web applications.
  应用程序层特定上下文，例如用于 Web 应用程序的 `WebApplicationContext` 。



在 Spring 中，构成应用程序主干并由 Spring IoC 容器管理的对象称为 bean。 bean 是一个由 Spring IoC 容器实例化、组装和管理的对象。否则，bean 只是应用程序中的众多对象之一。 Bean 以及它们之间的依赖关系反映在容器使用的配置元数据中。

## Container Overview

`org.springframework.context.ApplicationContext` 接口代表 Spring IoC 容器，负责实例化、配置和组装 bean。容器通过读取配置元数据来获取要实例化、配置和组装哪些对象的指令。配置元数据以 XML、Java 注释或 Java 代码表示。它可以让您表达组成应用程序的对象以及这些对象之间丰富的相互依赖性。

Spring 提供了 `ApplicationContext` 接口的多个实现。在独立应用程序中，通常创建 `ClassPathXmlApplicationContext` 或 `FileSystemXmlApplicationContext` 的实例。

### Spring 工作原理

![container magic](https://docs.spring.io/spring-framework/reference/_images/container-magic.png)

应用程序类与配置元数据相结合，以便在创建并初始化 `ApplicationContext` 后拥有一个完全配置且可执行的系统或应用程序。

### Configuration Metadata

Spring 配置由容器必须管理的至少一个（通常是多个）bean 定义组成。基于 XML 的配置元数据将这些 bean 配置为顶级 `<beans/>` 元素内的 `<bean/>` 元素。 Java 配置通常在 `@Configuration` 类中使用 `@Bean` 带注释的方法。



这些 bean 定义对应于构成应用程序的实际对象。通常，您定义服务层对象、持久层对象（例如存储库或数据访问对象 (DAO)）、表示对象（例如 Web 控制器）、基础结构对象（例如 JPA `EntityManagerFactory` 和 JMS 队列）等。通常，人们不会在容器中配置细粒度的域对象，因为创建和加载域对象通常是存储库和业务逻辑的责任。


以下示例显示了基于 XML 的配置元数据的基本结构：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans
		https://www.springframework.org/schema/beans/spring-beans.xsd">

	<bean id="..." class="...">  
		<!-- collaborators and configuration for this bean go here -->
	</bean>

	<bean id="..." class="...">
		<!-- collaborators and configuration for this bean go here -->
	</bean>

	<!-- more bean definitions go here -->

</beans>
Copied!
```

- `id` 属性是一个字符串，用于标识各个 bean 定义
- `class` 属性定义 bean 的类型并使用完全限定的类名



### Instantiating a Container

提供给 `ApplicationContext` 构造函数的位置路径是资源字符串，让容器从各种外部资源加载配置元数据，例如本地文件系统、Java `CLASSPATH` 、等等。

`````java
ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");
`````



### Using the Container

`ApplicationContext` 是高级工厂的接口，能够维护不同 bean 及其依赖项的注册表。通过使用方法 `T getBean(String name, Class<T> requiredType)` ，您可以检索 bean 的实例。

`ApplicationContext` 允许您读取 Bean 定义并访问它们

```java
// create and configure beans
ApplicationContext context = new ClassPathXmlApplicationContext("services.xml", "daos.xml");

// retrieve configured instance
PetStoreService service = context.getBean("petStore", PetStoreService.class);

// use configured instance
List<String> userList = service.getUsernameList();
```



## Bean Overview

Spring IoC 容器管理一个或多个 bean。这些 bean 是使用您提供给容器的配置元数据创建的（例如，以 XML `<bean/>` 定义的形式）		



### Instantiating Beans

Bean 定义本质上是创建一个或多个对象的配方。容器在被询问时查看命名 bean 的配方，并使用该 bean 定义封装的配置元数据来创建（或获取）实际对象。

如果您使用基于 XML 的配置元数据，则可以在 `<bean/>` 元素的 `class` 属性中指定要实例化的对象的类型（或类）。此 `class` 属性（在内部是 `BeanDefinition` 实例上的 `Class` 属性）通常是强制的。可以通过以下两种方式之一使用 `Class` 属性：

- 
  通常，在容器本身通过反射调用其构造函数直接创建bean的情况下指定要构造的bean类，有点相当于Java代码中的 `new` 运算符。
- 指定包含调用来创建对象的 `static` 工厂方法的实际类，在不太常见的情况下，容器调用类上的 `static` 工厂方法来创建 bean 。调用 `static` 工厂方法返回的对象类型可能是同一个类，也可能完全是另一个类。



### Instantiation with a Constructor

当通过构造函数方法创建 bean 时，所有普通类都可以由 Spring 使用并与 Spring 兼容。也就是说，正在开发的类不需要实现任何特定的接口或以特定的方式编码。只需指定 Bean 类就足够了。但是，根据对该特定 bean 使用的 IoC 类型，可能需要一个默认（空）构造函数。

使用基于 XML 的配置元数据可以指定 bean 类，如下所示：

```xml
<bean id="exampleBean" class="examples.ExampleBean"/>

<bean name="anotherExample" class="examples.ExampleBeanTwo"/>
```



### Instantiation with a Static Factory Method

定义使用静态工厂方法创建的 Bean 时，请使用 `class` 属性指定包含 `static` 工厂方法和名为 `factory-method` 的属性的类指定工厂方法本身的名称。

以下 bean 定义指定将通过调用工厂方法来创建 bean。该定义没有指定返回对象的类型（类），而是指定包含工厂方法的类。在此示例中， `createInstance()` 方法必须是 `static` 方法。

以下示例显示如何指定工厂方法：

```xml
<bean id="clientService"
	class="examples.ClientService"
	factory-method="createInstance"/>
```

以下示例显示了一个可与前面的 bean 定义一起使用的类：

```java
public class ClientService {
	private static ClientService clientService = new ClientService();
	private ClientService() {}

	public static ClientService createInstance() {
		return clientService;
	}
}
```



### Instantiation by Using an Instance Factory Method

与通过静态工厂方法实例化类似，使用实例工厂方法实例化会从容器中调用现有 bean 的非静态方法来创建新 bean。

要使用此机制，请将 `class` 属性留空，并在 `factory-bean` 属性中指定包含实例方法的当前（或父级或祖先）容器中 Bean 的名称将被调用来创建对象。使用 `factory-method` 属性设置工厂方法本身的名称。

以下示例展示了如何配置此类 bean：

```xml
<!-- the factory bean, which contains a method called createInstance() -->
<bean id="serviceLocator" class="examples.DefaultServiceLocator">
	<!-- inject any dependencies required by this locator bean -->
</bean>

<!-- the bean to be created via the factory bean -->
<bean id="clientService"
	factory-bean="serviceLocator"
	factory-method="createClientServiceInstance"/>
Copied!
```


以下示例显示了相应的类：

```java
public class DefaultServiceLocator {

	private static ClientService clientService = new ClientServiceImpl();

	public ClientService createClientServiceInstance() {
		return clientService;
	}
}
```



## Dependency Injection

依赖注入 (DI) 是一个过程，对象仅通过构造函数参数、工厂方法的参数或对象实例构造后设置的属性来定义其依赖项（即与它们一起工作的其他对象）。从工厂方法返回。然后，容器在创建 bean 时注入这些依赖项。这个过程从根本上来说是 bean 本身的逆过程（因此得名“控制反转”），通过使用类的直接构造或服务定位器模式自行控制其依赖项的实例化或位置。

采用 DI 原则，代码更加清晰，并且当对象提供其依赖项时，解耦更加有效。该对象不会查找其依赖项，也不知道依赖项的位置或类。因此，您的类变得更容易测试，特别是当依赖项位于接口或抽象基类上时，这允许在单元测试中使用存根或模拟实现。

DI 存在两种主要变体：基于构造函数的依赖注入和基于 Setter 的依赖注入。



### Constructor-based Dependency Injection

基于构造函数的 DI 是通过容器调用带有多个参数的构造函数来完成的，每个参数代表一个依赖项。调用具有特定参数的 `static` 工厂方法来构造 bean 几乎是等效的，并且本讨论以类似方式处理构造函数和 `static` 工厂方法的参数。

以下示例显示了一个只能通过构造函数注入进行依赖注入的类：

```java
public class SimpleMovieLister {

	// the SimpleMovieLister has a dependency on a MovieFinder
	private final MovieFinder movieFinder;

	// a constructor so that the Spring container can inject a MovieFinder
	public SimpleMovieLister(MovieFinder movieFinder) {
		this.movieFinder = movieFinder;
	}

	// business logic that actually uses the injected MovieFinder is omitted...
}
```

**请注意，这个类没有什么特别的。它是一个 POJO，不依赖于容器特定的接口、基类或注释。**

### Constructor Argument Resolution

构造函数参数解析匹配通过使用参数的类型进行。如果 bean 定义的构造函数参数中不存在潜在的歧义，则在 bean 定义中定义构造函数参数的顺序就是在实例化 bean 时将这些参数提供给适当的构造函数的顺序。

考虑下面的类：

```java
package x.y;

public class ThingOne {

	public ThingOne(ThingTwo thingTwo, ThingThree thingThree) {
		// ...
	}
}
```

假设 `ThingTwo` 和 `ThingThree` 类不通过继承相关，则不存在潜在的歧义。因此，以下配置可以正常工作，并且不需要在 `<constructor-arg/>` 元素中显式指定构造函数参数索引或类型。

```xml
<beans>
	<bean id="beanOne" class="x.y.ThingOne">
		<constructor-arg ref="beanTwo"/>
		<constructor-arg ref="beanThree"/>
	</bean>

	<bean id="beanTwo" class="x.y.ThingTwo"/>

	<bean id="beanThree" class="x.y.ThingThree"/>
</beans>
```

当引用另一个 bean 时，类型是已知的，并且可以发生匹配（如前面示例的情况）。当使用简单类型时，例如 `<value>true</value>` ，Spring 无法确定值的类型，因此无法在没有帮助的情况下按类型进行匹配。

考虑下面的类：

```java
package examples;

public class ExampleBean {

	// Number of years to calculate the Ultimate Answer
	private final int years;

	// The Answer to Life, the Universe, and Everything
	private final String ultimateAnswer;

	public ExampleBean(int years, String ultimateAnswer) {
		this.years = years;
		this.ultimateAnswer = ultimateAnswer;
	}
}
```



#### Constructor argument type matching

在上述场景中，如果使用 `type` 属性显式指定构造函数参数的类型，则容器可以使用简单类型的类型匹配。

如下例所示：

```xml
<bean id="exampleBean" class="examples.ExampleBean">
	<constructor-arg type="int" value="7500000"/>
	<constructor-arg type="java.lang.String" value="42"/>
</bean>
```



#### Constructor argument index

可以使用 `index` 属性显式指定构造函数参数的索引。

如以下示例所示：

```xml
<bean id="exampleBean" class="examples.ExampleBean">
	<constructor-arg index="0" value="7500000"/>
	<constructor-arg index="1" value="42"/>
</bean>
```


除了解决多个简单值的歧义之外，指定索引还可以解决构造函数具有两个相同类型的参数时的歧义。

**The index is 0-based**



#### Constructor argument name

您还可以使用构造函数参数名称来消除值歧义，如以下示例所示：

```xml
<bean id="exampleBean" class="examples.ExampleBean">
	<constructor-arg name="years" value="7500000"/>
	<constructor-arg name="ultimateAnswer" value="42"/>
</bean>
```



请记住，为了使其开箱即用，您的代码必须在启用调试标志的情况下进行编译，以便 Spring 可以从构造函数中查找参数名称。如果您不能或不想使用调试标志编译代码，则可以使用 `@ConstructorProperties` JDK 注释来显式命名构造函数参数。

示例类必须如下所示：

```java
package examples;

public class ExampleBean {

	// Fields omitted

	@ConstructorProperties({"years", "ultimateAnswer"})
	public ExampleBean(int years, String ultimateAnswer) {
		this.years = years;
		this.ultimateAnswer = ultimateAnswer;
	}
}
```