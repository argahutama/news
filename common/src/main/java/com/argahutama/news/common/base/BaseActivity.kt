package com.argahutama.news.common.base

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.argahutama.news.common.R
import com.argahutama.news.common.connectivity.base.ConnectivityProvider
import com.argahutama.news.common.dialog.LoadingProgressDialog
import com.argahutama.news.common.navigation.NavigationDirection
import com.argahutama.news.common.navigation.NavigationPack
import com.argahutama.news.common.navigation.PendingNavigation
import com.thanosfisherman.mayi.MayI
import com.thanosfisherman.mayi.PermissionBean
import com.thanosfisherman.mayi.PermissionToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withLock

@Suppress("DEPRECATION")
abstract class BaseActivity<SuccessState> : AppCompatActivity() {
    abstract val binding: ViewBinding
    abstract val viewModel: BaseViewModel<SuccessState>

    open val fullscreen = false
    open val lightMode = false

    private var clickJob: Job? = null
    private val minLoadingDelayInMillisecond = 500L

    val connectivity: ConnectivityProvider by lazy { ConnectivityProvider.createProvider(this) }

    abstract fun render(state: SuccessState?)
    abstract fun setup()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        if (fullscreen) {
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
            window.statusBarColor = Color.TRANSPARENT
        }

        if (lightMode) {
            if (!fullscreen) window.statusBarColor = ContextCompat.getColor(this, R.color.white)
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        viewModel.viewState.observe(this, {
            if (it is BaseViewState.Loading) {
                showLoading()
                return@observe
            }

            lifecycleScope.launch {
                val delayTimeInMillisecond =
                    minLoadingDelayInMillisecond.minus(viewModel.actionTime.minus(viewModel.launchTime))
                when {
                    delayTimeInMillisecond < 0 -> hideLoading()
                    delayTimeInMillisecond > minLoadingDelayInMillisecond -> {
                        delay(minLoadingDelayInMillisecond)
                        hideLoading()
                    }
                    else -> {
                        delay(delayTimeInMillisecond)
                        hideLoading()
                    }
                }

                when (it) {
                    is BaseViewState.Error -> showError(it.throwable)
                    is BaseViewState.ConnectionError -> showConnectionError()
                    is BaseViewState.ServerError -> showServerError()
                    is BaseViewState.Success -> render(it.data)
                }
            }
        })

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        internalSetup()
        setup()
    }

    private fun internalSetup() {
        PendingNavigation.data.observe(this) {
            processAllPendingNavigation()
        }
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                processAllPendingNavigation()
            }
        })
    }

    private fun processAllPendingNavigation() {
        lifecycleScope.launch(Dispatchers.Main.immediate) {
            PendingNavigation.mutex.withLock {
                val data = PendingNavigation.data.value
                    ?.takeIf {
                        it.size > 0 && lifecycle.currentState == Lifecycle.State.RESUMED
                    }
                    ?: return@withLock
                data.forEach { processPendingNavigation(it) }
                PendingNavigation.clear()
            }
        }
    }

    private fun processPendingNavigation(nav: NavigationPack) {
        if (lifecycle.currentState != Lifecycle.State.RESUMED) return
        navigateTo(nav.navigation, nav.requestCode)
    }

    private fun showConnectionError() =
        navigateTo(NavigationDirection.ConnectionError, RC_GENERIC_ERROR)

    private fun showServerError() = navigateTo(NavigationDirection.ServerError, RC_GENERIC_ERROR)

    fun toast(message: String, length: Int = Toast.LENGTH_SHORT) =
        Toast.makeText(this, message, length).show()

    fun showError(throwable: Throwable) = toast(throwable.message.orEmpty())

    open fun showLoading() {
        lifecycleScope.launch {
            try {
                supportFragmentManager.executePendingTransactions()
            } catch (e: Throwable) {
            }

            val loadingDialog = (supportFragmentManager.findFragmentByTag(
                LoadingProgressDialog::class.java.canonicalName
            ) as LoadingProgressDialog?)
            if (loadingDialog == null) {
                LoadingProgressDialog.newInstance().show(
                    supportFragmentManager,
                    LoadingProgressDialog::class.java.canonicalName
                )
            }
        }
    }

    open fun hideLoading() {
        lifecycleScope.launch {
            try {
                supportFragmentManager.executePendingTransactions()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            val progressDialog = supportFragmentManager
                .findFragmentByTag(LoadingProgressDialog::class.java.canonicalName) as LoadingProgressDialog?
            progressDialog?.dismiss()
        }
    }

    fun getBaseApp() = application as? BaseApp

    fun schedule(direction: NavigationDirection, triggerAt: Long) =
        getBaseApp()?.schedule(direction, triggerAt)

    fun navigateTo(direction: NavigationDirection, requestCode: Int? = null) {
        if (requestCode == null) getBaseApp()?.navigateTo(this, direction)
        else getBaseApp()?.navigateTo(this, direction, requestCode)
    }

    fun requestPermission(
        permission: String,
        message: String = "",
        onSuccess: () -> Unit = {}, onDenied: () -> Unit = {},
    ) {
        if (permissionDialog != null) return
        if (ActivityCompat.checkSelfPermission(
                this, permission
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            MayI.withActivity(this)
                .withPermission(permission)
                .onResult { permissionResultSingle(it, message, onSuccess, onDenied) }
                .onRationale { _, token -> permissionRationale(token) }
                .check()
        } else {
            onSuccess()
        }
    }

    fun requestPermissions(
        permissions: List<String>,
        message: String = "",
        onSuccess: () -> Unit = {}, onDenied: () -> Unit = {},
    ) {
        if (permissionDialog != null) return
        if (permissions.any {
                ActivityCompat.checkSelfPermission(
                    this, it
                ) != PackageManager.PERMISSION_GRANTED
            }) {
            MayI.withActivity(this)
                .withPermissions(*permissions.toTypedArray())
                .onResult { permissionResultMulti(it, message, onSuccess, onDenied) }
                .onRationale { _, token -> permissionRationale(token) }
                .check()
        } else {
            onSuccess()
        }
    }

    private fun goToSettings() {
        val myAppSettings =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
        myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        myAppSettings.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        myAppSettings.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        startActivityForResult(myAppSettings, APP_SETTINGS_CODE)
    }

    private fun permissionResultSingle(
        permission: PermissionBean,
        message: String,
        onSuccess: () -> Unit, onDenied: () -> Unit,
    ) {
        when {
            permission.isPermanentlyDenied -> showPermissionQuestion(message, onDenied)
            permission.isGranted -> onSuccess()
            else -> onDenied()
        }
    }

    private var permissionDialog: AlertDialog? = null
    private fun showPermissionQuestion(message: String, onDenied: () -> Unit) {
        if (permissionDialog != null) return
        permissionDialog = AlertDialog.Builder(this).setMessage(message)
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.option_yes)
            ) { dialog, _ ->
                goToSettings()
                dialog.dismiss()
                lifecycleScope.launch {
                    delay(1000)
                    permissionDialog = null
                }
            }
            .setNegativeButton(
                getString(R.string.option_no)
            ) { dialog, _ ->
                dialog.dismiss()
                lifecycleScope.launch {
                    delay(1000)
                    permissionDialog = null
                }
                onDenied()
            }
            .create()
        permissionDialog?.show()
    }

    private fun permissionResultMulti(
        permission: List<PermissionBean>,
        message: String,
        onSuccess: () -> Unit, onDenied: () -> Unit,
    ) {
        when {
            permission.any { it.isPermanentlyDenied } -> showPermissionQuestion(message, onDenied)
            permission.all { it.isGranted } -> onSuccess()
            else -> onDenied()
        }
    }

    private fun permissionRationale(token: PermissionToken) {
        token.continuePermissionRequest()
    }

    open fun retry() {}

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_GENERIC_ERROR) {
            if (resultCode != RESULT_OK) finish()
        }
    }

    fun show(dialogFragment: DialogFragment, tag: String) {
        if (supportFragmentManager.findFragmentByTag(tag)?.isVisible != true)
            dialogFragment.show(supportFragmentManager, tag)
    }

    protected fun debounce(
        delayInMs: Long = 200L,
        action: () -> Unit
    ) {
        clickJob?.cancel()
        clickJob = lifecycleScope.launch {
            delay(delayInMs)
            action()
        }
    }

    companion object {
        const val RC_GENERIC_ERROR = 200
        const val APP_SETTINGS_CODE = 100
    }
}