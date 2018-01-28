package com.nateswartz.spheroapp

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.RippleDrawable
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView

/**
 * Created by nates on 1/27/2018.
 */
class ImageAdapter(private val mContext: Context) : BaseAdapter() {

    // references to our images
    val imgIds = arrayOf<Int>(
            R.drawable.grid_docmcstuffins,
            R.drawable.grid_daniel_tiger,
            R.drawable.grid_sesame_street,
            R.drawable.grid_elmos_song,
            R.drawable.grid_head_shoulders_knees_toes,
            R.drawable.grid_itsybitsyspider,
            R.drawable.grid_cookie_monster,
            R.drawable.grid_rubber_ducky)

    override fun getCount(): Int {
        return imgIds.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    // create a new ImageView for each item referenced by the Adapter
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView: ImageView
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = ImageView(mContext)
            //imageView.setLayoutParams(GridView.LayoutParams(85, 85))
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP)
            imageView.setPadding(8, 8, 8, 8)
        } else {
            imageView = (convertView as ImageView?)!!
        }
        val image = ContextCompat.getDrawable(mContext, imgIds[position])
        val rippledImage = RippleDrawable(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.primaryLightColor)), image, null)
        imageView.setImageDrawable(rippledImage)
        return imageView
    }
}