package com.shizy.androidtools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_id_number.*
import java.util.*

class IdNumberActivity : AppCompatActivity() {

    private val mRandom = Random()

    private var mProvinceNames: Array<String>? = null
    private var mProvinceCodes: Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_id_number)

        mProvinceNames = resources.getStringArray(R.array.province_name_list)
        mProvinceCodes = resources.getStringArray(R.array.province_code_list)
    }

    fun generate(view: View) {
        val builder = StringBuilder()
        val provinceIndex = Math.abs(mRandom.nextInt()) % mProvinceCodes!!.size
        // 加入城市编码
        builder.append(mProvinceCodes!![provinceIndex])
        // 加入城市地区编码
        builder.append(String.format(Locale.CHINA, "%04d", Math.abs(mRandom.nextInt()) % 10000))

        // 加入生日(随机20-60岁)
        val calendar = Calendar.getInstance()
        val age = Math.abs(mRandom.nextInt()) % 40 + 21
        val year = calendar.get(Calendar.YEAR) - age
        builder.append(year)
        val month = Math.abs(mRandom.nextInt()) % 12 + 1
        builder.append(String.format(Locale.CHINA, "%02d", month))
        calendar.clear()
        calendar.set(year, month, 1)
        val days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val day = Math.abs(mRandom.nextInt()) % days + 1
        builder.append(String.format(Locale.CHINA, "%02d", day))

        // 加入派出所的代码
        builder.append(String.format(Locale.CHINA, "%02d", Math.abs(mRandom.nextInt()) % 100))

        // 加入性别：第17位数字表示性别：奇数表示男性，偶数表示女性
        builder.append(Math.abs(mRandom.nextInt()) % 10)

        // 加入校检码
        builder.append(getCheckCode(builder.toString()))

        number.text = String.format(Locale.CHINA, "%s: %s", mProvinceNames!![provinceIndex], builder.toString())

        copy.tag = builder.toString()
    }

    private fun getCheckCode(number: String): Char {
        // 从右侧第2位开始向左，共17位。计算公式(2^(i - 1)) % 11
        val weight = intArrayOf(2, 4, 8, 5, 10, 9, 7, 3, 6, 1, 2, 4, 8, 5, 10, 9, 7)

        val code = charArrayOf('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2')

        var sum = 0
        for (i in 0 until number.length) {
            val value = Integer.parseInt(number[number.length - 1 - i] + "")
            sum += value * weight[i]
        }

        return code[sum % code.size]
    }

    fun copy(view: View) {
        val number = view.tag as? String

        number?.let {
            val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("Id Number", number)
            manager.primaryClip = clipData

            Toast.makeText(this, "已复制到剪切板！", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        fun start(context: Context) {
            val starter = Intent(context, IdNumberActivity::class.java)
            context.startActivity(starter)
        }
    }
}