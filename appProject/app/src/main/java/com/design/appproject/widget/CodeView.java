package com.design.appproject.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.ColorUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

//随机验证码
public class CodeView extends View {
    private String mTextViewText;

    public String getTextViewText() {
        return mTextViewText;
    }

    private int mTextViewTextSize;

    private Rect mBound;
    private Paint mPaint;

    public CodeView(Context context) {
        this(context, null);
    }

    public CodeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CodeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //获得自定义样式属性=
        mTextViewText = randomText();
        mTextViewTextSize = 80;
        mPaint = new Paint();
        mPaint.setTextSize(mTextViewTextSize);
        mBound = new Rect();
        mPaint.getTextBounds(mTextViewText, 0, mTextViewText.length(), mBound);
        //添加监听事件 用于 数字点击切换
        this.setOnClickListener(v -> {
            mTextViewText = randomText();
            //UI刷新
            postInvalidate();
        });
    }

    private List<String> chars = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k",
            "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
            "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G",
            "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z", "0", "1", "2",
            "3", "4", "5", "6", "7", "8", "9");

    private String randomText() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 4) {
            int randomInt = random.nextInt(chars.size());
            sb.append(chars.get(randomInt));
        }
        Log.d("randomText", "randomText:" + sb);
        return sb.toString();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Random random = new Random();
        //绘制view范围
        mPaint.setColor(Color.YELLOW);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        //绘制view颜色
        mPaint.setColor(ColorUtils.getRandomColor(false));
        canvas.drawText(mTextViewText, getWidth() / 2 - mBound.width() / 2,
                getHeight() / 2 + mBound.height() / 2, mPaint);
        //绘制黑点来模糊数字显示
        mPaint.setColor(Color.BLACK);
        for (int i = 0; i < 1000; i++) {
            canvas.drawCircle(random.nextInt(getMeasuredWidth()), random.nextInt(getMeasuredHeight()), 1, mPaint);
        }
        //绘制黑线同样为了模糊数字显示
        mPaint.setColor(Color.BLUE);
        for (int i = 0; i < 15; i++) {
            canvas.drawLine(random.nextInt(getMeasuredWidth()), random.nextInt(getMeasuredWidth()),
                    random.nextInt(getMeasuredWidth()), random.nextInt(getMeasuredWidth()), mPaint);
        }
    }
}
