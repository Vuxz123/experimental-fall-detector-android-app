package com.ethnicthv

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.webkit.WebView
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class About : Fragment(), View.OnClickListener {
    private var binding: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = inflater.inflate(R.layout.about, container, false)
        this.binding = binding

        val emergency = binding.findViewById<View>(R.id.emergency) as Button
        emergency.setOnClickListener(this)

        return binding
    }

    override fun onClick(view: View) {
        if (R.id.emergency == view.id) {
            Alarm.alert(requireActivity().applicationContext)
        }
    }

    override fun onStart() {
        super.onStart()
        refreshPermissions(true)

        val emergencyWrapper = binding?.findViewById<View>(R.id.emergency_wrapper)
        Log.d(TAG, "onStart :: Emergency Wrapper Animation :: ${emergencyWrapper?.height}")
        emergencyWrapper?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                emergencyWrapper.viewTreeObserver.removeOnGlobalLayoutListener(this)
                emergencyWrapper.translationY = emergencyWrapper.height.toFloat()
                emergencyWrapper
                    .animate()
                    .translationY(0f)
                    .setDuration(300)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            }
        })
    }

    private fun refreshPermissions(request: Boolean) {
        val statusText: String
        val statusColor: Int
        if (permitted(request)) {
            statusText = "Status: Ready"
            statusColor = Color.GREEN
        } else {
            statusText = "Status: Missing"
            statusColor = Color.RED
        }
        val binding = this.binding ?: return
    }

    private fun permitted(request: Boolean): Boolean {
        val list: MutableList<String> = mutableListOf()
        var granted = true
        for (item: VersionedPermission in PERMISSIONS) {
            list.add(item.permission)
            if (Build.VERSION.SDK_INT >= item.version && ContextCompat.checkSelfPermission(
                    requireActivity().applicationContext,
                    item.permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                granted = false
            }
        }
        if (!granted) {
            if (request) {
                requestPermissions.launch(list.toTypedArray())
            }
        }
        return granted
    }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var granted = true
            for (permission in permissions) {
                granted = granted && permission.value
            }
            if (!granted) {
                Guardian.say(
                    requireActivity().applicationContext,
                    android.util.Log.ERROR,
                    TAG,
                    "ERROR: Permissions were not granted"
                )
            }
            refreshPermissions(false)
        }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    class VersionedPermission(val permission: String, val version: Int)

    companion object {
        private val PERMISSIONS: Array<VersionedPermission> = arrayOf(
            VersionedPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Build.VERSION_CODES.JELLY_BEAN
            ),
            VersionedPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Build.VERSION_CODES.JELLY_BEAN
            ),
            VersionedPermission(
                Manifest.permission.CALL_PHONE,
                Build.VERSION_CODES.JELLY_BEAN
            ),
            VersionedPermission(
                Manifest.permission.CHANGE_WIFI_STATE,
                Build.VERSION_CODES.JELLY_BEAN
            ),
            VersionedPermission(
                Manifest.permission.INTERNET,
                Build.VERSION_CODES.JELLY_BEAN
            ),
            VersionedPermission(
                Manifest.permission.MODIFY_AUDIO_SETTINGS,
                Build.VERSION_CODES.JELLY_BEAN
            ),
            VersionedPermission(
                Manifest.permission.READ_CONTACTS,
                Build.VERSION_CODES.JELLY_BEAN
            ),
            VersionedPermission(
                Manifest.permission.READ_PHONE_STATE,
                Build.VERSION_CODES.JELLY_BEAN
            ),
            VersionedPermission(
                Manifest.permission.READ_CALL_LOG,
                Build.VERSION_CODES.JELLY_BEAN
            ),
            VersionedPermission(
                "android.permission.ANSWER_PHONE_CALLS",
                Build.VERSION_CODES.O
            ),
            VersionedPermission(
                Manifest.permission.RECEIVE_BOOT_COMPLETED,
                Build.VERSION_CODES.JELLY_BEAN
            ),
            VersionedPermission(
                Manifest.permission.RECEIVE_SMS,
                Build.VERSION_CODES.JELLY_BEAN
            ),
            VersionedPermission(
                Manifest.permission.SEND_SMS,
                Build.VERSION_CODES.JELLY_BEAN
            ),
            VersionedPermission(
                "android.permission.FOREGROUND_SERVICE",
                Build.VERSION_CODES.P
            ),
        )

        private val TAG: String = About::class.java.simpleName
    }
}