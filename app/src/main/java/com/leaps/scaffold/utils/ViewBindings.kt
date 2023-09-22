package com.leaps.scaffold.utils

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

// Activity
inline fun <reified VB : ViewBinding> Activity.binding() = lazy {
    inflateBinding<VB>(layoutInflater).apply { setContentView(root) }
}

// Fragment
inline fun <reified VB : ViewBinding> Fragment.binding() =
    FragmentBindingDelegate(VB::class.java)

// dialog
inline fun <reified VB : ViewBinding> Dialog.binding() = lazy {
    inflateBinding<VB>(layoutInflater).apply { setContentView(root) }
}

// ViewGroup
inline fun <reified VB : ViewBinding> ViewGroup.binding(attachToParent: Boolean = true): VB =
    inflateBinding(LayoutInflater.from(context), if (attachToParent) this else null, attachToParent)

// ViewGroup
/**
 * 如果根布局是merge标签，那么此XMl生成的[androidx.viewbinding.ViewBinding]方法中，只存在[android.view.LayoutInflater.inflate]
 * 的两个参数的方法： inflate(@LayoutRes int resource, @Nullable ViewGroup root)，因此，此时通过[ViewGroup.binding]反射去强行调用
 * 三个参数的inflate方法，会造成异常。
 *
 * 如果自定义View的xml根布局是merge标签，请使用此扩展方法
 */
inline fun <reified VB : ViewBinding> ViewGroup.bindingForMerge(): VB =
    inflateBindingForMerge(LayoutInflater.from(context), this)

inline fun <reified VB : ViewBinding> inflateBinding(layoutInflater: LayoutInflater) =
    VB::class.java.getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as VB

inline fun <reified VB : ViewBinding> inflateBinding(parent: ViewGroup) =
    inflateBinding<VB>(LayoutInflater.from(parent.context), parent, false)

inline fun <reified VB : ViewBinding> inflateBinding(
    layoutInflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean
) = VB::class.java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
    .invoke(null, layoutInflater, parent, attachToParent) as VB

inline fun <reified VB : ViewBinding> inflateBindingForMerge(
    layoutInflater: LayoutInflater, parent: ViewGroup?
) = VB::class.java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java)
    .invoke(null, layoutInflater, parent) as VB

inline fun <reified VB : ViewBinding> BindingViewHolder(parent: ViewGroup) =
    BindingViewHolder(inflateBinding<VB>(parent))

open class BindingViewHolder<VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root) {
    constructor(block: (LayoutInflater, ViewGroup, Boolean) -> VB, parent: ViewGroup) :
            this(block(LayoutInflater.from(parent.context), parent, false))
}

class FragmentBindingDelegate<VB : ViewBinding>(
    private val clazz: Class<VB>
) : ReadOnlyProperty<Fragment, VB> {

    private var binding: VB? = null

    private var fragment: Fragment? = null
    private val fragmentLifecycleCallbacks = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewDestroyed(fm: FragmentManager, f: Fragment) {
            if (fragment == f) {
                binding = null
                fragment = null
                fm.unregisterFragmentLifecycleCallbacks(this)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Fragment, property: KProperty<*>): VB {
        if (binding == null) {

            /**
             * [Lifecycle.State.DESTROYED]阶段[binding]值应为null，此阶段不允许再获取[binding]
             */
            val lifecycle = thisRef.viewLifecycleOwner.lifecycle
            if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
                throw IllegalStateException(
                    "FragmentBindingDelegate " + this + " is "
                            + "attempting to Initialization binding while current state is "
                            + lifecycle.currentState + ". binding must Initialization after "
                            + "lifecycle state is INITIALIZED."
                )
            }

            binding = if (thisRef.view != null) {
                clazz.getMethod("bind", View::class.java)
                    .invoke(null, thisRef.requireView()) as VB
            } else {
                val mContainer = try {
                    Reflect.on(thisRef).get<ViewGroup>("mContainer")
                } catch (e: Exception) {
                    null
                }
                clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
                    .invoke(null, LayoutInflater.from(thisRef.context), mContainer, false) as VB
            }

            /**
             * 此回调将会在[Fragment.onDestroyView]之后调用，因此不必再使用sendMessageAtFrontOfQueue的方式
             */
            fragment = thisRef
            thisRef.parentFragmentManager.registerFragmentLifecycleCallbacks(fragmentLifecycleCallbacks, false)
        }
        return binding!!
    }
}