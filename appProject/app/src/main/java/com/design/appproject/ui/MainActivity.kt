package com.design.appproject.ui

import android.view.KeyEvent
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.forEachIndexed
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.blankj.utilcode.util.AppUtils
import com.design.appproject.R
import com.design.appproject.base.BaseBindingActivity
import com.design.appproject.base.CommonArouteApi
import com.design.appproject.databinding.ActivityMainLayoutBinding
import com.union.union_basic.logger.LoggerManager

/**首页
 * */
@Route(path = CommonArouteApi.PATH_ACTIVITY_MAIN)
class MainActivity : BaseBindingActivity<ActivityMainLayoutBinding>() {

    @JvmField
    @Autowired
    var mIndex: Int = -1 /*切换*/

    private val fragments = mutableListOf(
        ARouter.getInstance().build(CommonArouteApi.PATH_FRAGMENT_INDEX).navigation() as Fragment,
        ARouter.getInstance().build(CommonArouteApi.PATH_FRAGMENT_LIST_SHIWUXINXI).withBoolean("mHasBack",false).navigation() as Fragment,
        ARouter.getInstance().build(CommonArouteApi.PATH_FRAGMENT_LIST_SHIQUXINXI).withBoolean("mHasBack",false).navigation() as Fragment,

        ARouter.getInstance().build(CommonArouteApi.PATH_FRAGMENT_MY).navigation() as Fragment,
    )

    override fun initEvent() {
        binding.initViewPager()
    }

    private fun ActivityMainLayoutBinding.initViewPager() {
        viewPager2.adapter = object : FragmentStateAdapter(this@MainActivity) {
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }
        viewPager2.isUserInputEnabled = false /*当ViewPager切换页面时，改变底部导航栏的状态*/
        bottomNav.itemIconTintList = null /*当ViewPager切换页面时，改变ViewPager的显示*/
        bottomNav.setOnItemSelectedListener {
            bottomNav.menu.forEachIndexed { index, item ->
                if (it.itemId == item.itemId){
                    viewPager2.setCurrentItem(index, false)
                }
                LoggerManager.d("it.itemId:${it.itemId}__item.itemId:${item.itemId}")
            }
            true
        }
        viewPager2.offscreenPageLimit = fragments.size
        viewPager2.currentItem = 0
        bottomNav.selectedItemId = R.id.navigation_index_home
    }

    private var exitTime: Long = 0

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(this, "再按一次退出${AppUtils.getAppName()}", Toast.LENGTH_SHORT).show()
                exitTime = System.currentTimeMillis()
            } else {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun switchFragment(tableName:String){
        val index=  binding.bottomNav.menu.children.indexOfFirst { it.itemId == R.id::class.java.getField("navigation_"+tableName+"_home").getInt(null) }
        binding.bottomNav.menu.getItem(index).isChecked = true
        binding.viewPager2.setCurrentItem(index, false)
    }
}