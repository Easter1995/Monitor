# Glimmer Backend Recruit



​        <img src="https://raw.githubusercontent.com/KMSorSMS/picGallery/master/img/202308022020500.jpg" />

## 招新说明

​		这里是Java的后端项目招新题，虽然是一个不太小的项目，但是请不要对难度太担心，考核内容偏向于代码理解，业务了解。并且目前在这个仓库里面的代码是可以运行的喔，登录部分是完整的代码，而其他的部分---摄像头部分、报警部分、后端交互部分是给出需要写的函数，并且给出了大量注释让你来填写。

​		说到这里，可能同学会觉得那难度是否会太简单，没有区分度，因为都只是填写代码而已，事实上如果你有基础，或者前面的题目做的很认真，那么到这里你需要的也只是仔细阅读登录部分的代码，然后根据需求完善后面的代码就好了。可以保证的是，登录部分里面的代码或者说写的逻辑，大量的在后面几个部分进行重复，只有较少的点会有一些更新。我们出题的初衷就是力求0基础同学能够做题，只要是踏实学的，就能顺利（当然时间会花）完成题目。而我们的区分度主要是在代码的规范性，自己对代码的理解情况，功能实现的完整性以及我们给出的一些加分环节(bonus)

​		之所以选择一个项目的方式和其它有些工作室招新的内容不同，是因为我们希望参加招新的同学在学习go或者是java的时候不要单纯的陷入语法的环节，并且不知道进入工作室最开始可能会做些什么，能拉近后面你和打比赛的距离，并且增加你在招新做题的趣味性，毕竟能自己做出来一个不算小的项目，对后端内容有一个基本认识，是一件让人激动的事情。我们的题目绝不是希望打消你学习的意愿。

​		接下来我会讲解项目架构

## 项目概述

​		这次项目的应用场景是一个通过人脸识别找犯罪嫌疑人并且发送警报信息的平台，而我们这里的后端，实际上是个服务器端，也就是一个架设在前端用户操作和处理视频内容的机器学习后端的一个中间服务器端。

​		他的功能很大一部分是与数据库相关，前端会给服务器端摄像头的参数信息，以及用户登录信息，以及摄像头的拍摄检测区域可能会有范围(注意是可能)，就是摄像头可能是只检测其拍摄区域里面的一个小矩形框记为(box)，一个摄像头可能会有很多很多的矩形框划定，这是看用户画了多少。然后还有给前端提供实时的报警信息推送，并让他能够下载报警视频以及报警图片(因为嫌疑人露脸应该是一个时间段，然后报警视频就是时间段的内容，报警的图片就是从里面截取的一张特征性图片)。

​		那你可能好奇那报警信息是怎么产生的呢？前面介绍的，我们只是前端和机器学习后端的一个中间服务器，报警信息的产生是由机器信息后端产生的，它会给你报警信息（图片、描述），然后再把后面的视频部分发给你(之所以是分开发送，是因为报警信息可能在嫌疑犯出来的前几秒就能出来，但是视频部分应该是完整的一整段视频，所以会有延后，分成了两个接口发送)，而后端也会需要你提供给他有关于摄像头的信息，比如摄像头的参数以及用户画的框(box)的情况。

------

## 该如何做题？

在这里，为了让题目变得更加友好，我们不要求你掌握git工具的使用，你只需要直接点击下载按钮就可以把基础项目的压缩包下载到本地啦，下载完后解压打开即可，可参考如下：

<img src="https://fastly.jsdelivr.net/gh/KMSorSMS/picGallery/img/202308021157213.png" alt="image-20230802103447929" style="zoom:50%;" />

**后续我们会将任务布置在相应的README的markdown文件中**🥰

------

## 前置引导

在这个部分，我们会引导你了解一下后端Java业务开发所应具备的相关知识，当然，后端Java的东西又多又杂😢，在目前这个阶段不需要你完全理解其中的原理以及熟练地运用，我们更加看重的是你的态度，其次才是学习能力，在项目中我们已为你搭建了相关环境并实现了一部分功能，并且你可以通过阅读相关注释来理解相关的代码逻辑，所以不必太过于担心啦！

### part 1.你了解Spring Framework吗？



当谈到`JavaWeb`开发时，Spring家族可以说是当下一个非常重要和受欢迎的选择，而Spring系列中最基础也是最重要的当属Spring Framework。那么我们为什么选用Spring Framework进行开发呢😊？

**首先**Spring Framework提供了一个轻量级的开发框架，可以帮助开发人员快速构建可维护和模块化的应用程序，**面向切面编程**等特性也使得开发过程更加简洁和高效。

**其次**它还可以通过**控制反转**与**依赖注入**特性来**解耦**各组件之间的关系，使得代码更容易测试、维护和扩展。

**当然**在`JavaWeb`开发中，数据一致性是至关重要的。Spring提供了强大的事务管理支持，可以简化数据库事务的控制和管理。

**最重要的是**Spring可以集成许多其他流行的Java技术和框架，这使得开发人员可以更轻松地使用这些技术，并且能够更好地利用它们的优点。

在进行项目开发之前希望你去了解(当然并不是什么强制要求啦)🥰：

该怎么构建的管理模块？**Maven：**请参考 [Maven 教程 | 菜鸟教程](https://www.runoob.com/maven/maven-tutorial.html)

什么是**Bean：**可参考 [Spring Bean](https://blog.csdn.net/suannai11/article/details/87898317)

或许你想了解**XML：**请参考 [XML 教程 | 菜鸟教程](https://www.runoob.com/xml/xml-tutorial.html)

为什么不建议new一个对象呢？**控制反转**与**依赖注入**：可参考 [快速理解IOC与DI](https://zhuanlan.zhihu.com/p/36840573)

你还不知道注解吗？请看看**注解开发**：可参考 [Java注解总结](https://zhuanlan.zhihu.com/p/85612062)

感兴趣的话，你还可以自己动手实践编码，在Maven中引入Spring的相关依赖，推荐使用注解开发，可以试试用`IOC`容器创建和管理Bean哦😁！(不强制要求)

这里给出一个用XML文件配置包扫描和数据库连接的示例：

<img src="https://raw.githubusercontent.com/1972701037/img/main/2023/08/upgit_20230818_1692327979.png" />

------

### part 2.来看看常见的业务开发框架吧！

在最早传统的Java业务开发中，企业使用的是`Servlet`+`JavaBean`+`JSP`的较为底层的技术进行开发，为实现"解耦、分层、代码复用"等目的，由此衍生出了`MVC`模式，并且以此提出了表现层、业务逻辑层、数据访问层三层架构，这里给出一个包结构示例：

<img src="https://raw.githubusercontent.com/1972701037/img/main/2023/08/upgit_20230818_1692328078.png" />

接下来，让我们一起来看看什么是MVC模式和三层架构吧：[基础 | 三层架构与MVC模式](https://zhuanlan.zhihu.com/p/101038664)

看完相关的介绍后，想必你已经对相关知识有一定的理解了，同样Spring也帮我们内置集成了MVC框架，后面我们的项目也会按照三层架构的模式来写，一般我们只要按需实现表现层、业务层以及持久层的相关代码逻辑就可以啦😊。

说了这么多，怎么能少了后端最重要的数据库呢😭？在前面，想必你已经对MySQL这类关系型数据库有所了解，并且用JDBC实现过Java客户端对数据库的操作，但是你会发现其中有很多代码都重复冗余，因此强烈建议你了解使用MyBatis框架来简化开发啦，它可以利用Mapper映射将数据库字段属性值完美封装在Java对象属性值中来喔，推荐参考：

[什么是MyBatis](https://blog.csdn.net/weixin_51418964/article/details/128696139)

当然，我们推荐你自己动手实践编码，试试建立MVC模式的包结构，在Maven中引入MyBatis与数据库驱动的相关依赖，并用MyBatis框架完成对数据库的CRUD操作吧😁！(不强制要求啦)

这里给出一个MVC的包结构示例：

<img src="https://raw.githubusercontent.com/1972701037/img/main/2023/08/upgit_20230818_1692328363.png" />

**假如**你想实现用户模块登录功能，你就可以在Controller包下创建一个UserController类交予Spring进行管理，并添加相应的方法，其中的返回数据类型和接受数据类型以及接口路径参数都是你可以自定义的，如下是一个简单的例子：

```java
@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping(value = "/login")
    @ResponseBody
    public User UserGet(User user){
        //这里写具体的代码逻辑
    }
}
```

**当然**，我们一般是不会将具体业务部分的代码直接全写在控制层的，而是需要你创建Service层的接口以及其相关实现类，并且将相关业务方法写在Service层的实现类中去，而在控制层只需要注入Service层的接口并且直接调用业务方法即可，如下是一个简单的例子：

```java
@Controller
@RequestMapping("/user")
public class UserController {
    //注入用户Service接口
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login")
    @ResponseBody
    public User UserGet(User user){
        return userService.login(user);
    }
}
```

**同样**，在Service实现类中要完成具体的业务，往往需要与数据库打交道，这里你就可以使用MyBatis框架的Mapper啦，这里同样需要将Service实现类以及Mapper接口也交给Spring容器来管理，之后你只需要在Service层的实现类中注入相应的Mapper接口即可，具体如下：

```java
@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(User user) {
        //查询数据库中的数据
        User user = userMapper.get(user);
        //处理具体的业务逻辑
       
        //返回user对象
        return user;
    }
```

------

### part 3.嗯？apifox？

最后的最后，要进行后端的业务开发，你必须要了解接口文档的使用，而apifox是一款很好的接口文档管理工具，除此之外，还集成了许多其他强大的功能😊。它的下载不用多说，它的使用的话基本可以参照b站视频 ---- [apifox基础使用](https://www.bilibili.com/video/BV1ae4y1y7bf/?spm_id_from=333.999.0.0&vd_source=2af7d0ce02dcbfd4607844767c60beda)，这是官方的视频，我们也只是用了一小部分功能，后面你会在做题的过程中慢慢涉及到这些功能，完全不用担心这个介绍视频有很多名词比如: 接口，响应，请求参数等等听不懂，因为这些是后面你要学习的内容，也是我们这个后面task1任务所要去了解的。

再增加一些可能会用到的关于apifox使用的资料链接：

[apifox mock使用](https://apifox.com/help/api-mock/intro-to-mock)

[apifox 动态生成请求值](https://apifox.com/help/environment-and-variables/dynamic-values)

[我们项目的apifox的公开链接（永久有效）](https://apifox.com/apidoc/shared-b08df5b9-3511-4c37-9dee-e5e577d0c908)这个文档就是我们的apifox的文档，我们项目完全是参照这个api定义来的，当然有些示例不是很完整，没有去涵盖所有异常情况的响应，因为异常的处理也是我们考察的一个点。

你可以根据上面的这个文档来在apifox app里面创建，当然我们这个文档也是由apifox app里面的项目api发布出来的，

这是邀请链接---[apifox app邀请成员](https://app.apifox.com/invite/project?token=Jd6mDst3DFX7s16TnBGRu)

只是这个邀请链接有效期只能7天，所以有时候可能过期了，不过上面的文档内容其实和这个是一样的，所以都可以啦。

------

**提醒，题目需求和后续的task都会放在MonitorProject目录下的markdown文件中，请在相应的markdown文件中查看哦**🥰**！**
