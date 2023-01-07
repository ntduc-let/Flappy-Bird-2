package com.ntduc.flappybird.customview

import android.content.Context
import android.content.res.Configuration
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.ntduc.flappybird.R
import me.dm7.barcodescanner.core.DisplayUtils
import me.dm7.barcodescanner.core.IViewFinder

class CustomViewFinderView : View, IViewFinder {
    companion object {
        private const val PORTRAIT_WIDTH_RATIO = 6f / 8
        private const val PORTRAIT_WIDTH_HEIGHT_RATIO = 0.75f
        private const val LANDSCAPE_HEIGHT_RATIO = 5f / 8
        private const val LANDSCAPE_WIDTH_HEIGHT_RATIO = 1.4f
        private const val MIN_DIMENSION_DIFF = 50
        private const val DEFAULT_SQUARE_DIMENSION_RATIO = 5f / 8
//        private val SCANNER_ALPHA = intArrayOf(0, 64, 128, 192, 255, 192, 128, 64)
        private const val POINT_SIZE = 100
        private const val ANIMATION_DELAY = 0L
    }

    private var mFramingRect: Rect? = null

    //    private var scannerAlpha = 0
    private var scannerPosition = 0
    private val mDefaultLaserColor = ContextCompat.getColor(context, R.color.red)
    private val mDefaultMaskColor = ContextCompat.getColor(context, R.color.black_50)
    private val mDefaultBorderColor = ContextCompat.getColor(context, R.color.blue)
    private val mDefaultBorderStrokeWidth = resources.getInteger(R.integer.viewfinder_border_width)
    private val mDefaultBorderLineLength = resources.getInteger(R.integer.viewfinder_border_length)
    protected var mLaserPaint: Paint? = null
    protected var mFinderMaskPaint: Paint? = null
    protected var mBorderPaint: Paint? = null
    protected var mBorderLineLength = 0
    protected var mSquareViewFinder = false
    private var mIsLaserEnabled = false
    private var mBordersAlpha = 0f
    private var mViewFinderOffset = 0

    private val mPaint = Paint()


    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attributeSet: AttributeSet?) : super(context, attributeSet) {
        init()
    }

    private fun init() {
        //set up laser paint
        mLaserPaint = Paint()
        mLaserPaint!!.color = mDefaultLaserColor
        mLaserPaint!!.style = Paint.Style.FILL

        //finder mask paint
        mFinderMaskPaint = Paint()
        mFinderMaskPaint!!.color = mDefaultMaskColor

        //border paint
        mBorderPaint = Paint()
        mBorderPaint!!.color = mDefaultBorderColor
        mBorderPaint!!.style = Paint.Style.STROKE
        mBorderPaint!!.strokeWidth = mDefaultBorderStrokeWidth.toFloat()
        mBorderPaint!!.isAntiAlias = true
        mBorderLineLength = mDefaultBorderLineLength

        initCustom()
    }

    private fun initCustom() {
        mPaint.color = Color.WHITE
        mPaint.isAntiAlias = true
        setBorderColor(ContextCompat.getColor(context, R.color.blue))
        setLaserColor(ContextCompat.getColor(context, R.color.blue))
        setBorderLineLength(64)
        mPaint.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            40f,
            this.resources.displayMetrics
        )
        setSquareViewFinder(true)
    }

    override fun setLaserColor(laserColor: Int) {
        mLaserPaint!!.color = laserColor
    }

    override fun setMaskColor(maskColor: Int) {
        mFinderMaskPaint!!.color = maskColor
    }

    override fun setBorderColor(borderColor: Int) {
        mBorderPaint!!.color = borderColor
    }

    override fun setBorderStrokeWidth(borderStrokeWidth: Int) {
        mBorderPaint!!.strokeWidth = borderStrokeWidth.toFloat()
    }

    override fun setBorderLineLength(borderLineLength: Int) {
        mBorderLineLength = borderLineLength
    }

    override fun setLaserEnabled(isLaserEnabled: Boolean) {
        mIsLaserEnabled = isLaserEnabled
    }

    override fun setBorderCornerRounded(isBorderCornersRounded: Boolean) {
        if (isBorderCornersRounded) {
            mBorderPaint!!.strokeJoin = Paint.Join.ROUND
        } else {
            mBorderPaint!!.strokeJoin = Paint.Join.BEVEL
        }
    }

    override fun setBorderAlpha(alpha: Float) {
        val colorAlpha = (255 * alpha).toInt()
        mBordersAlpha = alpha
        mBorderPaint!!.alpha = colorAlpha
    }

    override fun setBorderCornerRadius(borderCornersRadius: Int) {
        mBorderPaint!!.pathEffect = CornerPathEffect(borderCornersRadius.toFloat())
    }

    override fun setViewFinderOffset(offset: Int) {
        mViewFinderOffset = offset
    }

    override fun setSquareViewFinder(set: Boolean) {
        mSquareViewFinder = set
    }

    override fun setupViewFinder() {
        updateFramingRect()
        invalidate()
    }

    override fun getFramingRect(): Rect {
        return mFramingRect!!
    }

    public override fun onDraw(canvas: Canvas) {
        drawViewFinderMask(canvas)
        drawViewFinderBorder(canvas)
        if (mIsLaserEnabled) {
            drawLaser(canvas)
        }
    }

    fun drawViewFinderMask(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height
        val framingRect = framingRect
        canvas.drawRect(0f, 0f, width.toFloat(), framingRect.top.toFloat(), mFinderMaskPaint!!)
        canvas.drawRect(
            0f,
            framingRect.top.toFloat(),
            framingRect.left.toFloat(),
            (framingRect.bottom + 1).toFloat(),
            mFinderMaskPaint!!
        )
        canvas.drawRect(
            (framingRect.right + 1).toFloat(),
            framingRect.top.toFloat(),
            width.toFloat(),
            (framingRect.bottom + 1).toFloat(),
            mFinderMaskPaint!!
        )
        canvas.drawRect(
            0f,
            (framingRect.bottom + 1).toFloat(),
            width.toFloat(),
            height.toFloat(),
            mFinderMaskPaint!!
        )
    }

    fun drawViewFinderBorder(canvas: Canvas) {
        val framingRect = framingRect

        // Top-left corner
        val path = Path()
        path.moveTo(framingRect.left.toFloat(), (framingRect.top + mBorderLineLength).toFloat())
        path.lineTo(framingRect.left.toFloat(), framingRect.top.toFloat())
        path.lineTo((framingRect.left + mBorderLineLength).toFloat(), framingRect.top.toFloat())
        canvas.drawPath(path, mBorderPaint!!)

        // Top-right corner
        path.moveTo(framingRect.right.toFloat(), (framingRect.top + mBorderLineLength).toFloat())
        path.lineTo(framingRect.right.toFloat(), framingRect.top.toFloat())
        path.lineTo((framingRect.right - mBorderLineLength).toFloat(), framingRect.top.toFloat())
        canvas.drawPath(path, mBorderPaint!!)

        // Bottom-right corner
        path.moveTo(framingRect.right.toFloat(), (framingRect.bottom - mBorderLineLength).toFloat())
        path.lineTo(framingRect.right.toFloat(), framingRect.bottom.toFloat())
        path.lineTo((framingRect.right - mBorderLineLength).toFloat(), framingRect.bottom.toFloat())
        canvas.drawPath(path, mBorderPaint!!)

        // Bottom-left corner
        path.moveTo(framingRect.left.toFloat(), (framingRect.bottom - mBorderLineLength).toFloat())
        path.lineTo(framingRect.left.toFloat(), framingRect.bottom.toFloat())
        path.lineTo((framingRect.left + mBorderLineLength).toFloat(), framingRect.bottom.toFloat())
        canvas.drawPath(path, mBorderPaint!!)
    }

    fun drawLaser(canvas: Canvas) {
        val framingRect = framingRect

//        // Draw a red "laser scanner" line through the middle to show decoding is active
//        mLaserPaint!!.alpha = SCANNER_ALPHA[scannerAlpha]
//        scannerAlpha = (scannerAlpha + 1) % SCANNER_ALPHA.size
//        val middle = framingRect.height() / 2 + framingRect.top
//        canvas.drawRect(
//            (framingRect.left + 2).toFloat(),
//            (middle - 1).toFloat(),
//            (framingRect.right - 1).toFloat(),
//            (middle + 2).toFloat(),
//            mLaserPaint!!
//        )
//        postInvalidateDelayed(
//            ANIMATION_DELAY,
//            framingRect.left - POINT_SIZE,
//            framingRect.top - POINT_SIZE,
//            framingRect.right + POINT_SIZE,
//            framingRect.bottom + POINT_SIZE
//        )

        val left = (framingRect.left + 2)
        val right = (framingRect.right - 1)
        val top = framingRect.top
        val bottom = framingRect.bottom

        if (scannerPosition == 0 || scannerPosition > bottom){
            scannerPosition = top+1
        }

        canvas.drawRect(
            left.toFloat(),
            (scannerPosition - 2).toFloat(),
            right.toFloat(),
            (scannerPosition + 3).toFloat(),
            mLaserPaint!!
        )
        scannerPosition+=2

        postInvalidateDelayed(
            ANIMATION_DELAY,
            framingRect.left - POINT_SIZE,
            framingRect.top - POINT_SIZE,
            framingRect.right + POINT_SIZE,
            framingRect.bottom + POINT_SIZE
        )
    }

    override fun onSizeChanged(xNew: Int, yNew: Int, xOld: Int, yOld: Int) {
        updateFramingRect()
    }

    @Synchronized
    fun updateFramingRect() {
        val viewResolution = Point(width, height)
        var width: Int
        var height: Int
        val orientation = DisplayUtils.getScreenOrientation(context)
        if (mSquareViewFinder) {
            if (orientation != Configuration.ORIENTATION_PORTRAIT) {
                height = (getHeight() * DEFAULT_SQUARE_DIMENSION_RATIO).toInt()
                width = height
            } else {
                width = (getWidth() * DEFAULT_SQUARE_DIMENSION_RATIO).toInt()
                height = width
            }
        } else {
            if (orientation != Configuration.ORIENTATION_PORTRAIT) {
                height = (getHeight() * LANDSCAPE_HEIGHT_RATIO).toInt()
                width = (LANDSCAPE_WIDTH_HEIGHT_RATIO * height).toInt()
            } else {
                width = (getWidth() * PORTRAIT_WIDTH_RATIO).toInt()
                height = (PORTRAIT_WIDTH_HEIGHT_RATIO * width).toInt()
            }
        }
        if (width > getWidth()) {
            width = getWidth() - MIN_DIMENSION_DIFF
        }
        if (height > getHeight()) {
            height = getHeight() - MIN_DIMENSION_DIFF
        }
        val leftOffset = (viewResolution.x - width) / 2
        val topOffset = (viewResolution.y - height) / 2
        mFramingRect = Rect(
            leftOffset + mViewFinderOffset,
            topOffset + mViewFinderOffset,
            leftOffset + width - mViewFinderOffset,
            topOffset + height - mViewFinderOffset
        )
    }
}