# blade
# 1. gradle 导入方式
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
    implementation 'com.github.BeauteousJade.blade:inject:1.0'
    annotationProcessor 'com.github.BeauteousJade.blade:processor:1.0'
}
```

# 2. 基本结构
&emsp;&emsp;在整个DI框架，有三个注解，分别是`Module`, `Inject`, `Provides`，先来解释这三个注解所表示的意思。

|注解|含义|使用范围|
|---|---|---|
|Module|需要注入的对象所在类需要标记该类|比如A类内部有一个B类需要从外部(Context)注入进来，B类必须标记`Moudle`,其中`Moudle`注解带一个Class参数，表示从哪个`Context`对象注入。|
|Inject|如果一个变量需要注入，那么该变量会被标记`Inject`，表示注入的目标|比如上面的B类对象，必须标记`Inject`。该注解默认带一个String参数(可以不填)， 表示以此String字符串为id从注入源取得注入对象;不填表示默认以该对象的`ClassName`为id。因此，在同一个Moudle下，不能同时有两个相同类型的变量的`Inject`注解不填id。|
|Provides|如果一个变量需要被注入，该变量会标记`Provides`注解。需要被注入的变量必须放在一个Context（Object类型，也就是任意类型）里面；反之，如果一个类里面有一个变量标记了`Provides`注解，表示该类可以作为一个Context|`Provides`默认带一个String参数，表示注入的id，跟`Inject`的参数相对应。|

&emsp;&emsp;如果使用注解成功的标记我们想要被注入和注入的变量，我们可以在通过调用`Injector`的`inject(Object target, Object source)`方法进行注入操作的最后一步，也只有经过这一步，想要注入的变量才会成功赋值。如下是`Injector`的代码：
```
public class Injector {

    public static void inject(Object target, Object source) {
        String className = target.getClass().getName() + "_Inject";
        try {
            Object object = Class.forName(className).newInstance();
            Class<?> clazz = object.getClass();
            Method method = clazz.getMethod("inject", target.getClass(), source.getClass());
            method.invoke(object, target, source);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
```
&emsp;&emsp;这也是本框架唯一通过反射方式来实现的地方。

&emsp;&emsp;在编译器时期，APT会扫面每个被`Moudle`标记的类，然后对应着会生成一个Java类，上面代码的作用就是调用生成的Java类的`inject`方法进行赋值。下面是一个Demo代码：
```
public class MainActivity_Inject {
  public void inject(MainActivity target, MainActivity.Context source) {
    target.string = source.string;
    target.a = source.a;
  }
}
```
# 3. 基本使用
&emsp;&emsp;首先我们有一个类的有一些变量注入，比如`MainActivity`
```
@Module(MainActivity.Context.class)
public class MainActivity extends AppCompatActivity {

    @Inject
    String string;
    @Inject
    int a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Injector.inject(this, new Context());
    }
    // ······
}
```
&emsp;&emsp;这里需要注意两点：
>1. `MainActivity`必须标记`Module`注解，同时`Module`注解必须传入注入源的Class对象。
>2. 注入的变量需要标记`Inject`

&emsp;&emsp;然后我们定义一个Context对象：
```
@Module(MainActivity.Context.class)
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
&emsp;&emsp;最后在调用`Injector`的`inject`方法。
```
        Injector.inject(this, new Context());
```
&emsp;&emsp;整个注入过程就完成了。
# 4. 参考框架
>1. [dagger](https://github.com/google/dagger)
>2. [butterKnife](https://github.com/JakeWharton/butterknife)
>3. [AptPreferences](https://github.com/joyrun/AptPreferences)
