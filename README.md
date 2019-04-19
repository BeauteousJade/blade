&emsp;&emsp;之前学习很多的框架，我个人觉得学习框架的目的主要两点：
>1. 理解框架的原理，在实际开发中，才会得心应手，游刃有余。
>2. 学习框架的设计思想和技术亮点。我觉得这才是学习框架的最终目的，只有学到了东西，才算是对自己有提升。

&emsp;&emsp;今天，我写这篇文章主要是为了介绍我的第一个框架--`Blade`。`Blade`其实不是非常厉害的框架，就是基于APT技术开发的依赖注入框架，简称DI框架。
# 1. 概述
&emsp;&emsp;其实，熟悉Java开发的同学应该都知道，Google有一个非常著名的依赖注入框架--`Dagger`，那我为什么还要自己开发一个依赖注入的框架呢？俗话说，重复造轮子是对轮子最大的不尊重。

&emsp;&emsp;当然我也不是吃饱了撑的没事干(其实仔细想一想，还是有那么一点)，这里面是有一定的原因。最近，楼主做自己的毕业设计，想在自己的毕业设计中加上依赖注入的框架，使得写的代码更加简洁。介于这个原因，我去学习了Google爸爸的`Dagger`框架。在学习过程中，我发现`Dagger`的一个设计，就是它提供对象都是通过方法来实现的，这个与我的想法有点相悖(有可能还有其他方式可以实现，但是我不知道)。由于这个原因，我就狠下心，打算自己来写一个，自己想要什么都可以自己来实现，然后入坑了。。。

&emsp;&emsp;在这框架开发过程中，我借鉴了`Dagger`的部分思想。其实，我们可以从框架命名可以看出来，`Dagger`的中文意思是`匕首`，而`Blade`是剑的意思，看上去也是非常相似，以表示我对`Dagger`的尊重。

&emsp;&emsp;接下来，我将简单介绍一下`Blade`。
# 2. gradle 导入方式
### (1). 项目的build.gradle
```
allprojects {
    repositories {
        // ······
        maven { url 'https://jitpack.io' }
    }
}
```
### (2). app的build.gradle
```
dependencies {
    // ······
    implementation 'com.github.BeauteousJade.blade:inject:latest version'
    annotationProcessor 'com.github.BeauteousJade.blade:processor:latest version'
}
```

# 3. 基本结构
&emsp;&emsp;在整个DI框架，有两个注解，分别是`Inject`, `Provides`，先来解释这三个注解所表示的意思。

|注解|含义|使用范围|
|---|---|---|
|Inject|如果一个变量需要注入，那么该变量会被标记`Inject`，表示注入的目标|比如上面的B类对象，必须标记`Inject`。该注解默认带一个String参数(可以不填)， 表示以此String字符串为id从注入源取得注入对象;不填表示默认以该对象的`ClassName`为id。因此，在同一个Module下，不能同时有两个相同类型的变量的`Inject`注解不填id。|
|Provides|如果一个变量需要被注入，该变量会标记`Provides`注解。需要被注入的变量必须放在一个Context（Object类型，也就是任意类型）里面；反之，如果一个类里面有一个变量标记了`Provides`注解，表示该类可以作为一个Context|`Provides`默认带一个String参数，表示注入的id，跟`Inject`的参数相对应。|

&emsp;&emsp;如果使用注解成功的标记我们想要被注入和注入的变量，我们可以在通过调用`Blade`的`inject(Object target, Object source)`方法进行注入操作的最后一步，也只有经过这一步，想要注入的变量才会成功赋值。
# 4. 基本使用
&emsp;&emsp;首先我们有一个类的有一些变量注入，比如`MainActivity`
```
public class MainActivity extends AppCompatActivity {

    @Inject
    String string;
    @Inject
    int a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Blade.inject(this, new Context());
    }
    // ······
}
```
&emsp;&emsp;这里需要注意的是：
>1. 注入的变量需要标记`Inject`。
>2. 如果注入源带有Id，inject必须带有id。

&emsp;&emsp;然后我们定义一个Context对象：
```
public class MainActivity extends AppCompatActivity {

    // ······
    public static class Context {
        @Provides
        String string = "string";
        @Provides
        int a = 1;
    }
}
```
&emsp;&emsp;这里需要注意的是：
>1. 作为数据源的变量需要标记`Provides `。
>2. 建议`Provides `带id，保证唯一性。如果不带id，在同一个`Context`树中，同类型的数据源有且只能有一个。

&emsp;&emsp;最后在调用`Blade`的`inject`方法。
```
        Blade.inject(this, new Context());
```
&emsp;&emsp;整个注入过程就完成了。
# 5. github地址
&emsp;&emsp; github:[blade](https://github.com/BeauteousJade/blade)
# 6. 参考框架
>1. [dagger](https://github.com/google/dagger)
>2. [butterKnife](https://github.com/JakeWharton/butterknife)
>3. [AptPreferences](https://github.com/joyrun/AptPreferences)
# 7. blog
>1. [Blade - 基本使用](https://www.jianshu.com/p/32ca48a6e05e)
>2. [Blade - 1.4版本重大更新](https://www.jianshu.com/p/65002b459042)

