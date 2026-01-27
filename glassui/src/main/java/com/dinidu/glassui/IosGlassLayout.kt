package com.dinidu.glassui

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.content.withStyledAttributes

class IosGlassLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val snapshotView = ImageView(context).apply {
        scaleType = ImageView.ScaleType.CENTER_CROP
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    private val overlayView = View(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        setBackgroundResource(R.drawable.bg_glass_overlay_ios)
    }

    // ✅ bypass addView overrides while adding internal layers
    private var internalAdding = false

    private var blurRadius: Float = 32f
    private var vibrancy: Boolean = true
    private var saturation: Float = 1.25f
    private var brightnessLift: Float = 6f
    private var autoUpdate: Boolean = true

    private var lastW = 0
    private var lastH = 0

    private val preDrawListener = ViewTreeObserver.OnPreDrawListener {
        if (autoUpdate) refresh()
        true
    }

    init {
        clipToPadding = false
        clipChildren = true

        context.withStyledAttributes(attrs, R.styleable.IosGlassLayout) {
            blurRadius = getFloat(R.styleable.IosGlassLayout_glassBlurRadius, 32f)
            vibrancy = getBoolean(R.styleable.IosGlassLayout_glassVibrancy, true)
            saturation = getFloat(R.styleable.IosGlassLayout_glassSaturation, 1.25f)
            brightnessLift = getFloat(R.styleable.IosGlassLayout_glassBrightnessLift, 6f)
            autoUpdate = getBoolean(R.styleable.IosGlassLayout_glassAutoUpdate, true)
        }

        // ✅ add internal layers first
        internalAdding = true
        super.addView(snapshotView, 0)
        super.addView(overlayView, 1)
        internalAdding = false

        // ✅ Round-corner clipping (VERY important for iOS look)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            clipToOutline = true
            outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    val r = resources.getDimension(R.dimen.glass_radius)
                    outline.setRoundRect(0, 0, view.width, view.height, r)
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        viewTreeObserver.addOnPreDrawListener(preDrawListener)
        post { refresh() }
    }

    override fun onDetachedFromWindow() {
        viewTreeObserver.removeOnPreDrawListener(preDrawListener)
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw || h != oldh) post { refresh() }
    }

    fun refresh() {
        if (width <= 0 || height <= 0) return

        if (!autoUpdate && width == lastW && height == lastH && snapshotView.drawable != null) return
        lastW = width
        lastH = height

        val root = rootView ?: return

        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)

        val loc = IntArray(2)
        val rootLoc = IntArray(2)
        getLocationOnScreen(loc)
        root.getLocationOnScreen(rootLoc)

        val dx = (loc[0] - rootLoc[0]).toFloat()
        val dy = (loc[1] - rootLoc[1]).toFloat()

        canvas.translate(-dx, -dy)
        root.draw(canvas)

        val finalBmp = if (vibrancy) makeVibrant(bmp, saturation, brightnessLift) else bmp
        snapshotView.setImageBitmap(finalBmp)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            snapshotView.setRenderEffect(
                RenderEffect.createBlurEffect(blurRadius, blurRadius, Shader.TileMode.CLAMP)
            )
        } else {
            snapshotView.setRenderEffect(null)
        }
    }

    private fun makeVibrant(src: Bitmap, saturation: Float, brightnessLift: Float): Bitmap {
        val out = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
        val c = Canvas(out)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val cm = ColorMatrix().apply { setSaturation(saturation) }
        val brighten = ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, brightnessLift,
                0f, 1f, 0f, 0f, brightnessLift,
                0f, 0f, 1f, 0f, brightnessLift,
                0f, 0f, 0f, 1f, 0f
            )
        )
        cm.postConcat(brighten)

        paint.colorFilter = ColorMatrixColorFilter(cm)
        c.drawBitmap(src, 0f, 0f, paint)
        return out
    }

    // ✅ user content always above snapshot+overlay
    override fun addView(child: View?) {
        if (internalAdding) { super.addView(child); return }
        val safeIndex = if (childCount < 2) childCount else 2
        super.addView(child, safeIndex)
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        if (internalAdding) { super.addView(child, params); return }
        val safeIndex = if (childCount < 2) childCount else 2
        super.addView(child, safeIndex, params)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (internalAdding) { super.addView(child, index, params); return }
        val base = if (childCount < 2) childCount else 2
        val safeIndex = if (index < base) base else index
        super.addView(child, safeIndex, params)
    }
}
