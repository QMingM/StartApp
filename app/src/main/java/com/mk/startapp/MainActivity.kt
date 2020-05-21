package com.mk.startapp

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val wxPackageName = "com.tencent.wework"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)//保持屏幕常亮
        setSupportActionBar(toolbar)

        fab.setOnClickListener { _ ->
            startTime()
        }

        fab.setOnLongClickListener { _ ->
            tv.visibility = View.GONE
            et.visibility = View.VISIBLE
            fab.isClickable = true
            setOnLongClickListener@ true
        }
    }

    private fun startTime() {
        val time = et.text.toString()
        if (time.isNotEmpty()) {
            val countDownTimer = object : CountDownTimer(time.toLong() * 60000, 60000) {
                override fun onTick(millisUntilFinished: Long) {
                    tv.visibility = View.VISIBLE
                    et.visibility = View.GONE
                    fab.isClickable = false
                    tv.text = (millisUntilFinished / 60000).toString()
                }

                override fun onFinish() {
                    doStartApplicationWithPackageName()
                }
            }
            countDownTimer.start()
        }
    }

    private fun doStartApplicationWithPackageName() {
        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = packageManager.getPackageInfo(wxPackageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (packageInfo == null) {
            return
        }
        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        val resolveIntent = Intent(Intent.ACTION_MAIN, null)
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        resolveIntent.setPackage(packageInfo.packageName)

        // 通过getPackageManager()的queryIntentActivities方法遍历
        val resolveInfoList = packageManager
            .queryIntentActivities(resolveIntent, 0)
        val resolveInfo = resolveInfoList.iterator().next()
        if (resolveInfo != null) {
            // packagename = 参数packname
            val packageName = resolveInfo.activityInfo.packageName
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            val className = resolveInfo.activityInfo.name
            // LAUNCHER Intent
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(Intent.CATEGORY_LAUNCHER)

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            val cn = ComponentName(packageName, className)
            intent.component = cn
            startActivity(intent)
        }
    }
}
