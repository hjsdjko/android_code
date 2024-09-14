package com.design.appproject.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.ColorUtils
import com.design.appproject.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.qmuiteam.qmui.alpha.QMUIAlphaTextView
import com.union.union_basic.ext.cssdp
import com.union.union_basic.ext.dp
import com.union.union_basic.ext.isNotNullOrEmpty
import com.union.union_basic.ext.no
import com.union.union_basic.ext.otherwise
import com.union.union_basic.ext.toConversion
import com.union.union_basic.ext.yes
import com.union.union_basic.logger.LoggerManager

/**自定义的Spinner*/
class BottomSpinner : QMUIAlphaTextView, View.OnClickListener {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
        setOnClickListener(this)
    }
    private val mCssParseHelper by lazy {
        CssParseHelper()
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        attrs?.let { mCssParseHelper.init(context, it,this) }
    }

    var options: List<String> = emptyList()
    private var onItemSelectedListener: OnItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(position: Int, content: String) {
            super.onItemSelected(position, content)
            text = content
        }

        override fun onItemMultipleSelected(positions: List<Int>, contents: List<String>) {
            super.onItemMultipleSelected(positions, contents)
            text = contents.joinToString()
        }
    }

    var mIsMultiple = false
    var mTitle = ""

    fun setOptions(options: List<String>, title: String = "", isMultiple: Boolean = false,listener: OnItemSelectedListener?=null) {
        this.options = options
        mIsMultiple = isMultiple
        mTitle = title
        listener?.let {
            this.onItemSelectedListener = listener
        }
    }

    fun setOnItemSelectedListener(listener: OnItemSelectedListener) {
        this.onItemSelectedListener = listener
    }

    private val dialog by lazy {
        BottomSheetDialog(context, R.style.BottomSheetDialogStyle).apply {
            val view = LayoutInflater.from(context).inflate(R.layout.layout_bottom_spinner, null)
            val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter =
                BottomSpinnerAdapter(options, this, mIsMultiple, onItemSelectedListener)
            view.findViewById<TextView>(R.id.cancel_btn).let {
                it.isVisible = mIsMultiple
                it.setOnClickListener { this.dismiss() }
            }
            view.findViewById<TextView>(R.id.spinner_title_tv).text = mTitle

            view.findViewById<RelativeLayout>(R.id.title_rl).isVisible =
                mIsMultiple || mTitle.isNotNullOrEmpty()
            view.findViewById<TextView>(R.id.ensure_btn).let {
                it.isVisible = mIsMultiple
                it.setOnClickListener {
                    val selectedPositionList =
                        recyclerView.adapter?.toConversion<BottomSpinnerAdapter>()
                            ?.getSelectedPositionList() ?: listOf()
                    val contents = options.filterIndexed { index, s ->
                        selectedPositionList.contains(index)
                    }
                    onItemSelectedListener?.onItemMultipleSelected(selectedPositionList, contents)
                    this.dismiss()
                }
            }

            this.setContentView(view)
            behavior.maxHeight = 500.dp
            this.setOnShowListener {
                recyclerView.adapter =
                    BottomSpinnerAdapter(options, this, mIsMultiple, onItemSelectedListener)
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?.let { mCssParseHelper.dispatchDraw(it) }
    }

    override fun onClick(v: View?) {
        dialog.show()
    }

    fun dialogShow() {
        dialog.show()
    }

    interface OnItemSelectedListener {
        fun onItemSelected(position: Int, content: String) {}
        fun onItemMultipleSelected(positions: List<Int>, contents: List<String>) {}
    }
}

class BottomSpinnerAdapter(
    private val options: List<String>,
    private val dialog: BottomSheetDialog,
    private val isMultiple: Boolean,
    private val onItemSelectedListener: BottomSpinner.OnItemSelectedListener?
) : RecyclerView.Adapter<BottomSpinnerAdapter.ViewHolder>() {

    private val selectedPositionList = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_bottom_spinner, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(options[position])
    }

    override fun getItemCount() = options.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val optionTextView: CheckedTextView = itemView.findViewById(R.id.text_option)
        fun bind(option: String) {
            optionTextView.text = option
            optionTextView.setChecked(selectedPositionList.contains(layoutPosition))
            optionTextView.setCheckedTextViewOnClickListener {
                isMultiple.no {
                    selectedPositionList.clear()
                    selectedPositionList.add(layoutPosition)
                    onItemSelectedListener?.onItemSelected(
                        layoutPosition,
                        optionTextView.text.toString()
                    )
                    dialog.dismiss()
                }.otherwise {
                    if (selectedPositionList.contains(layoutPosition)) {
                        selectedPositionList.remove(layoutPosition)
                    } else {
                        selectedPositionList.add(layoutPosition)
                    }
                }
            }
        }
    }

    fun getSelectedPositionList() = selectedPositionList
}

/**自定义右侧打勾textview*/
class CheckedTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    private var isChecked = false
    private var checkDrawable: Drawable = CheckDrawable(context).apply {
        setBounds(0, 0, 20.dp, 20.dp)
    }

    fun setCheckedTextViewOnClickListener(onClick: () -> Unit) {
        setOnClickListener {
            isChecked = !isChecked
            updateDrawable()
            onClick.invoke()
        }
    }

    // 更新TextView右边的图标
    private fun updateDrawable() {
        val drawables = compoundDrawablesRelative
        val drawable = if (isChecked) checkDrawable else null
        setCompoundDrawables(
            drawables[0],
            drawables[1], drawable, drawables[3]
        )
    }

    // 设置TextView是否被选中
    fun setChecked(checked: Boolean) {
        if (isChecked != checked) {
            isChecked = checked
            updateDrawable()
        }
    }

    // 获取TextView是否被选中
    fun isChecked() = isChecked
}

/**打勾图标*/
class CheckDrawable(context: Context) : Drawable() {

    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 6f
        color = ColorUtils.getColor(R.color.common_colorPrimary)
        strokeCap = Paint.Cap.ROUND
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        // 绘制打勾线条
        val path = Path().apply {
            moveTo(bounds.left + 0.25f * bounds.width(), bounds.top + 0.5f * bounds.height())
            lineTo(bounds.left + 0.4f * bounds.width(), bounds.top + 0.65f * bounds.height())
            lineTo(bounds.left + 0.75f * bounds.width(), bounds.top + 0.35f * bounds.height())
        }
        canvas.drawPath(path, paint)
    }

    override fun setAlpha(alpha: Int) {}

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }
}