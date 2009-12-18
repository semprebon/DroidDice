/* Copyright (C) 2009 Andrew Semprebon */
package com.droiddice;

//TODO: Add shadow to dice?

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class DieView extends View {

	private Bitmap mDieImage;
	private Die mDie;
	private int mDisplay = DISPLAY_VALUE;
	private TextPaint mTextPaint;
	private int mPreferredSize;
	private int mSize = 60;

	private static final Die DEFAULT_DIE = new SimpleDie(6);

	public static final int DISPLAY_BLANK = 0;
	public static final int DISPLAY_VALUE = 1;
	public static final int DISPLAY_TYPE = 2;

	public static final int SIZE_LARGE = 60;
	public static final int SIZE_MEDIUM = 30;
	public static final int SIZE_SMALL = 15;
	public static final int[] DIE_SIZES = { SIZE_LARGE, SIZE_MEDIUM, SIZE_SMALL };
	public static final int MIN_DIE_SIZE = DIE_SIZES[DIE_SIZES.length - 1];

	public static final String TAG = "DieView";

	public static final HashMap<String, Integer> TYPE_TO_IMAGE_ID = new HashMap<String, Integer>();
	static {
		TYPE_TO_IMAGE_ID.put("d4", R.drawable.d4_60);
		TYPE_TO_IMAGE_ID.put("d6", R.drawable.d6_60);
		TYPE_TO_IMAGE_ID.put("d8", R.drawable.d8_60);
		TYPE_TO_IMAGE_ID.put("d10", R.drawable.d10_60);
		TYPE_TO_IMAGE_ID.put("d12", R.drawable.d12_60);
		TYPE_TO_IMAGE_ID.put("d20", R.drawable.d20_60);
		TYPE_TO_IMAGE_ID.put("d100", R.drawable.d100_60);
	}
	
	public DieView(Context context) {
		super(context);
		setDie(DEFAULT_DIE);
		initialize();
	}

	public DieView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public DieView(Context context, AttributeSet attrs, int defaultStyle) {
		super(context, attrs, defaultStyle);
		setDie(DEFAULT_DIE);
		initialize();
	}

	private final void initialize() {
		mTextPaint = new TextPaint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setColor(getResources().getColor(R.color.text_dark));
		mTextPaint.setTextAlign(Paint.Align.CENTER);
		mTextPaint.setFakeBoldText(true);
		int padding = getContext().getResources().getDimensionPixelSize(R.dimen.die_padding);
		setPadding(padding, padding, padding, padding);
		mPreferredSize = SIZE_MEDIUM;
		mDieImage = BitmapFactory.decodeResource(getResources(), R.drawable.d6_60);
		mSize = 60;
	}

	/* Properties */

	public Die getDie() {
		return mDie;
	}

	public void setDie(Die die) {
		mDie = die;
	}

	public int getDisplay() {
		return mDisplay;
	}

	public void setDisplay(int display) {
		mDisplay = display;
	}

	public int getPreferredSize() {
		return mPreferredSize;
	}

	public void setPreferredSize(int preferredSize) {
		mPreferredSize = preferredSize;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int width = getWidth() - (getPaddingLeft() + getPaddingRight());
		int height = getHeight() - (getPaddingTop() + getPaddingBottom());
		int xOffset = (width - mSize) / 2 + getPaddingTop();
		int yOffset = (height - mSize) / 2 + getPaddingLeft();
		int shadowHeight = 14;
		
		Paint paint = new Paint();
		paint.setColorFilter(new LightingColorFilter(0xffffd9, 0));
		if (mDieImage != null) {
			canvas.drawBitmap(mDieImage, null, 
					new Rect(xOffset, yOffset, xOffset + mSize, yOffset + mSize), paint);
		}

		if (mDisplay == DISPLAY_VALUE) {
			drawValue(canvas, mSize * 0.6F, mDie.getDisplayValue());
		} else if (mDisplay == DISPLAY_TYPE) {
			drawValue(canvas, mSize * 0.4F, mDie.toString());
		}

		super.onDraw(canvas);
	}

	private void drawValue(Canvas canvas, float size, String text) {
		mTextPaint.setTextSize(size);
		int width = getWidth() - (getPaddingLeft() + getPaddingRight());
		int height = getHeight() - (getPaddingTop() + getPaddingBottom());
		int xOffset = width / 2 + getPaddingLeft();
		int tHeight = (int) (-mTextPaint.descent()-mTextPaint.ascent());
		int yOffset = (height + tHeight) / 2 + getPaddingTop();
		canvas.drawText(text, xOffset, yOffset, mTextPaint);
	}

	/**
	 * @see android.view.View#measure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = mPreferredSize + getPaddingLeft() + getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = mPreferredSize + getPaddingTop() + getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (changed) {
			loadImage(right - left, bottom - top);
		}
	}

	private void loadImage(int width, int height) {
		mSize = findBestSize(width, height);
		if (mDie instanceof AdjustmentDie) {
			mDieImage = null;
		} else {
			int imageId = TYPE_TO_IMAGE_ID.get(mDie.toString());
			Log.d(TAG, "Loading image " + imageId + " for " + mDie.toString());
			mDieImage = BitmapFactory.decodeResource(getResources(), imageId);
		}
	}

	private int findBestSize(int width, int height) {
		int maxSize = (width < height) ? width : height;
		for (int size : DIE_SIZES) {
			if (size <= maxSize) return size;
		}
		return MIN_DIE_SIZE;
	}
}
