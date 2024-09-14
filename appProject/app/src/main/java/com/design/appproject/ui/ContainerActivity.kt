package com.design.appproject.ui

import android.content.Intent
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.design.appproject.R
import com.design.appproject.base.BaseBindingActivity
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.databinding.ActivityContainerLayoutBinding

@Route(path = CommonArouteApi.PATH_ACTIVITY_CONTAINER)
class ContainerActivity : BaseBindingActivity<ActivityContainerLayoutBinding>() {

    @Autowired
    @JvmField
    var mParams: Map<String,Any>? = mapOf()/*传递参数*/

    @Autowired
    @JvmField
    var mPath: String = ""/*界面地址*/

    lateinit var mFragment:Fragment

    override fun initEvent() {
        initFragment(mPath,mParams)
    }

    private fun initFragment(path:String, params:Map<String,Any>?){
        val postcard = ARouter.getInstance().build(path)
        params?.forEach {
            when(it.value){
                is String->postcard.withString(it.key, it.value as String)
                is Int->postcard.withInt(it.key, it.value as Int)
                is Long->postcard.withLong(it.key, it.value as Long)
                is Boolean->postcard.withBoolean(it.key, it.value as Boolean)
                else -> {
                    if (it.value!=null){
                        postcard.withObject(it.key, it.value)
                    }
                }
            }
        }
        mFragment = postcard.navigation() as Fragment
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_fl, mFragment).commitNow()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mFragment.onActivityResult(requestCode,resultCode,data)
    }
}