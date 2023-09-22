package com.leaps.scaffold.paradigm;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

import java.util.ArrayList;
import java.util.List;

/**
 * https://rengwuxian.com/kotlin-generics/
 * @param <T>
 */
public class ParadigmJava<T extends TextView> {

    private Context mContext;

    public void test1() {
        //Button类是 TextView 的子类，声明类是TextView，而实例是 Button，这是 Java 的多态
        TextView textView = new Button(mContext);

        //如果加上泛型，那么这种多态关系就不成立了
        List<Button> buttons = new ArrayList<>();
        //下面这句话会报错，因为Java 认为 List<Button>并不是List<TextView>的子类
        //List<TextView> textViews = buttons;

        //为什么要这样设计？
        //Java 的泛型会在编译阶段进行泛型擦除。也就是说编译出来的class 类文件格式是没有相关泛型信息的
        //如果泛型没有上限，那么会统一被擦除为 Object，如果有上限，那么被擦除为上限的类型
        //这样讲的话存进去是没有什么问题的，但是取数据的话救就会出现一些转换异常

        //假如上面那句代码编译不出错，那么我们就可以 set 进去一个 TextView
        //buttons.add(textView);
        //当时当我们去取一个 Button 数据时，那么它其实是一个 TextView，这种将父类强转为子类是不被允许的，会出现类型转换异常 ClassCastException
        Button myButton = buttons.get(0);

        //为了安全，这样限制会失去泛型的灵活性，因此 Java 提供了 ? extend T、? super T、? 去提升 API 的灵活性

        //? extends T 叫做上界通配符，可以使Java 泛型具有 协变性
        //当使用了 ? extends 后，那么 TextView、TextView 的直接子类、TextView 的间接子类都可以进行下面的赋值操作
        List<? extends TextView> textViews = buttons;
        List<? extends TextView> textViews1 = new ArrayList<TextView>();//TextView
        List<? extends TextView> textViews2 = new ArrayList<Button>(); // 直接子类
        List<? extends TextView> textViews3 = new ArrayList<AppCompatButton>(); //间接子类

        //那么这种方式会带来什么样的限制呢
        //textViews.add(textView); 编译不通过
        textViews.get(0);
        textViews.remove(1);
        //可以看出，此时不允许对集合进行 add 操作了。
        //? extends TextView表示，只要满足该类是 TextView 的子类就可以，但是具体是什么类型，是不知道的
        //因此如果不限制 add 操作，那根本不知道里面存的是什么类型，那么后续通过 get 方法取值时就可能发生类型转换异常

        //由于 add 的这个限制，使用了 ? extends 泛型通配符的 List，只能够向外提供数据被消费，
        // 从这个角度来讲，向外提供数据的一方称为「生产者 Producer」。对应的还有一个概念叫「消费者 Consumer」

        //与之对应的是? super TextView，叫做下界通配符，使Java 泛型具有逆变性。
        //当使用了 ? super 后，那么 TextView、TextView 的直接父类类、TextView 的间接父类类都可以进行下面的赋值操作
        List<? super TextView> textViews4 = new ArrayList<TextView>();//TextView
        List<? super TextView> textViews5 = new ArrayList<View>(); // 直接父类
        List<? super TextView> textViews6 = new ArrayList<Object>(); //间接父类

        Object text = textViews4.get(1);
        textViews4.add(myButton);
        //可以看出可以对 Button 类型进行 add 操作了，但是 get 方法取出的只能是 Object 对象
        //? super TextView表示，该泛型类型一定是 TextView的父类，那么就代表add 进去 TextView 或者 TextView的子类是可以的
        //因为 add 进去的这个类它的直接或间接父类一定是这个泛型。但是因为 TextView 的父类类型众多，那么取得时候只能确定是共同父类 Object 类型
    }

}
