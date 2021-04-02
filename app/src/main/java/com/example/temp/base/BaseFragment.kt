package com.example.temp.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.example.temp.base.BaseViewModel.Companion.BUNDLE_ARGS

abstract class BaseFragment(@LayoutRes resId: Int) : Fragment(resId) {

    override fun setArguments(args: Bundle?) {
        if (args != null) {
            super.setArguments(Bundle(args).apply {
                putBundle(BUNDLE_ARGS, args)
            })
        } else {
            super.setArguments(null)
        }
    }
}