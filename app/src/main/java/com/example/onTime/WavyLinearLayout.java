package com.example.onTime;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class WavyLinearLayout extends LinearLayout {

    private Paint paint;
    private Path path;

    public WavyLinearLayout(Context context) {
        super(context);
        this.init(context);
    }

    public WavyLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    private void init(Context context){
        this.paint = new Paint();
        paint.setColor(Color.parseColor("#428BCA"));
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        this.path = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*float midX = this.getWidth() / 2.0f;
        float midY = this.getHeight() / 2.0f;

        this.path.moveTo(0, midY);
        this.path.quadTo(midX / 2, midY + (midY / 6), midX, midY);
        this.path.quadTo(1.5f * midX, midY - (midY / 6), 2 * midX, midY);
        this.path.quadTo(2 * midX, midY, 2 * midX, midY - 2 * midY);
        this.path.quadTo(2 * midX, midY - 2 * midY, 0, midY - 2 * midY);
        this.path.quadTo(0, 2 * midY, 0, midY);*/

        float width = this.getWidth();
        float height = this.getHeight();

        this.path.moveTo(0, 0);
        this.path.quadTo(width, 0, width, 0);
        this.path.quadTo(width, 0, width, height - (height / 8));
        this.path.quadTo(3 * (width / 4), height - (height / 4), width / 2, height - (height / 8));
        this.path.quadTo(width / 4, height, 0, height - (height / 8));
        this.path.quadTo(0, height, 0, 0);

        canvas.drawPath(this.path, this.paint);

    }
}
