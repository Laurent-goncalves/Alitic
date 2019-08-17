package com.g.laurent.alitic.Controllers.DialogFragments

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.Activities.MainActivity
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

class SettingsDialog : DialogFragment(), EasyPermissions.PermissionCallbacks{

    private lateinit var contextDialog: Context
    private var nameUser:String? = null

    fun newInstance(): SettingsDialog {
        return SettingsDialog()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.settings_layout, container)

        // Configure views
        configureViews(view)

        return view
    }

    private fun configureViews(view: View) {

        val prefs = contextDialog.getSharedPreferences(SHAREDPREF, 0)

        configureFieldName(view, prefs)
        configureExportExcel(view)
        configureReset(view)
        configureButtonSave(view, prefs)
    }

    private fun configureFieldName(view: View, prefs: SharedPreferences){

        val nameView = view.findViewById<EditText>(R.id.name_user)
        nameUser = prefs.getString(NAME_USER, null)

        // init
        nameView.setText(nameUser)

        // Listener change
        nameView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                nameUser = s.toString()
            }
        })
    }

    private fun configureExportExcel(view: View){
        val buttonExcel = view.findViewById<ImageButton>(R.id.button_export)
        buttonExcel.setOnClickListener {
            checkPermissionWriteExternalStorage()
        }
    }

    private fun checkPermissionWriteExternalStorage(){

        val perms = Manifest.permission.WRITE_EXTERNAL_STORAGE

        if (!EasyPermissions.hasPermissions(contextDialog, perms)) {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_write),
                PERMISSION_WRITE_STORAGE, perms)
        } else {
            onPermissionsGranted(PERMISSION_WRITE_STORAGE, mutableListOf(perms))
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(contextDialog, contextDialog.resources.getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        // Check if a file already exists
        val pathDownload = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val path = "$pathDownload/${contextDialog.resources.getString(R.string.excel_file_name)}"
        val file = File(path)

        if(file.exists()){

            val builder = AlertDialog.Builder(contextDialog)

            // Display a message on alert dialog
            builder.setTitle(contextDialog.resources.getString(R.string.replace_excel_file_title)) // TITLE
            builder.setMessage(contextDialog.resources.getString(R.string.replace_excel_file_message)) // MESSAGE

            // Set positive button and its click listener on alert dialog
            builder.setPositiveButton(contextDialog.resources.getString(R.string.yes)){ dialog, _ ->
                dialog.dismiss()
                writeToExcelFile(file, contextDialog)
            }

            // Display negative button on alert dialog
            builder.setNegativeButton(contextDialog.resources.getString(R.string.no)){ dialog, _ ->
                dialog.dismiss()
            }

            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
            dialog.show()

        } else {
            writeToExcelFile(file, contextDialog)
        }
    }

    private fun configureReset(view: View){
        val buttonReset = view.findViewById<Button>(R.id.button_reset)
        buttonReset.setOnClickListener {
            val activity = activity
            if(activity is ResetDatabaseListener){
                activity.emptyDatabase()
            }
        }
    }

    private fun configureButtonSave(view: View, prefs: SharedPreferences) {

        val buttonSave = view.findViewById<Button>(R.id.button_save)
        buttonSave.setOnClickListener {
            // Save name in shared pref
            prefs.edit().putString(NAME_USER, nameUser).apply()
            Toast.makeText(contextDialog, contextDialog.resources.getString(R.string.data_saved), Toast.LENGTH_LONG).show()

            this.dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        val activity = activity as MainActivity
        activity.updateToolbarTitle()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextDialog = context
    }
}

interface ResetDatabaseListener {
    fun emptyDatabase()
}