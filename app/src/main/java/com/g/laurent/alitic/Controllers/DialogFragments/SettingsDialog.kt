package com.g.laurent.alitic.Controllers.DialogFragments

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.g.laurent.alitic.*
import com.g.laurent.alitic.Controllers.Activities.MainActivity
import com.g.laurent.alitic.Controllers.ClassControllers.getAllEventTypes
import com.g.laurent.alitic.Controllers.ClassControllers.getAllFoodTypes
import com.g.laurent.alitic.Controllers.ClassControllers.getListFoodByType
import com.g.laurent.alitic.Controllers.ClassControllers.updateFood
import com.g.laurent.alitic.Models.EventType
import com.g.laurent.alitic.Models.Food
import com.g.laurent.alitic.Models.FoodType
import com.g.laurent.alitic.Views.EventTypeSettingsAdapter
import com.g.laurent.alitic.Views.FoodSettingsAdapter
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

interface ResetDatabaseListener {
    fun emptyDatabase()
}

class SettingsDialog : DialogFragment(), EasyPermissions.PermissionCallbacks{

    private lateinit var contextDialog: Context
    private var nameUser:String? = null
    private lateinit var onFoodEventSettingsClick:OnFoodEventSettingsClick

    fun newInstance(): SettingsDialog {
        return SettingsDialog()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_settings_layout, container)

        if(activity is OnFoodEventSettingsClick)
            onFoodEventSettingsClick = activity as OnFoodEventSettingsClick

        // Configure views
        configureViews(view)

        return view
    }

    private fun configureViews(view: View) {

        val prefs = contextDialog.getSharedPreferences(SHAREDPREF, 0)

        configureFieldName(view, prefs)
        configureExportExcel(view)
        configureReset(view)
        configureButtonFoodsSettings(view)
        configureButtons(view, prefs)
        configureLegalNotice(view)
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
        val buttonReset = view.findViewById<ImageButton>(R.id.button_reset)
        buttonReset.setOnClickListener {
            val activity = activity
            if(activity is ResetDatabaseListener){
                activity.emptyDatabase()
            }
        }
    }

    private fun configureButtonFoodsSettings(view: View){
        val buttonFoodSettings = view.findViewById<ImageButton>(R.id.button_settings_foods)
        buttonFoodSettings.setOnClickListener{
            onFoodEventSettingsClick.showFoodSettingsDialog()
        }

        val buttonEventSettings = view.findViewById<ImageButton>(R.id.button_settings_events)
        buttonEventSettings.setOnClickListener{
            onFoodEventSettingsClick.showEventSettingsDialog()
        }
    }

    private fun configureButtons(view: View, prefs: SharedPreferences) {

        val buttonQuit = view.findViewById<Button>(R.id.button_quit)
        buttonQuit.setOnClickListener {
            this.dismiss()
        }

        val buttonSave = view.findViewById<Button>(R.id.button_save)
        buttonSave.setOnClickListener {
            // Save name in shared pref
            prefs.edit().putString(NAME_USER, nameUser).apply()
            Toast.makeText(contextDialog, contextDialog.resources.getString(R.string.data_saved), Toast.LENGTH_LONG).show()

            this.dismiss()
        }
    }

    private fun configureLegalNotice(view: View) {

        view.findViewById<ImageView>(R.id.legal_notice).setOnClickListener {

            val alertDialog = AlertDialog.Builder(contextDialog)
            alertDialog.setTitle(contextDialog.resources.getString(R.string.legal_notice_title))
            alertDialog.setMessage(getLegalNotice(contextDialog))

            alertDialog.setPositiveButton(contextDialog.resources.getString(R.string.ok)){ dialog, _ ->
                dialog.dismiss()
            }

            // Finally, make the alert dialog using builder
            val legNotDialog: AlertDialog = alertDialog.create()

            // Display the alert dialog on app interface
            legNotDialog.show()
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

class FoodSettingsDialog : DialogFragment() {

    private lateinit var contextDialog: Context
    private lateinit var foodTypeSelected: FoodType
    private val listFoodChanged = mutableListOf<Food>()
    private val listFood = mutableListOf<Food>()
    private lateinit var listFoodTypes:List<FoodType>
    private lateinit var onFoodEventSettingsClick:OnFoodEventSettingsClick

    fun newInstance(): FoodSettingsDialog {
        return FoodSettingsDialog()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.settings_layout, container)

        // init listFoodTypes
        val listFoodTypesTemp = getAllFoodTypes(false, contextDialog)

        // Configure title and table
        if(listFoodTypesTemp!=null){
            listFoodTypes = listFoodTypesTemp

            if(activity is OnFoodEventSettingsClick)
                onFoodEventSettingsClick = activity as OnFoodEventSettingsClick

            configureListFoodTypes(view)
            configureViews(view)
        } else {
            Toast.makeText(contextDialog, contextDialog.resources.getString(R.string.no_foodtype_found), Toast.LENGTH_LONG).show()
            dismiss()
        }

        return view
    }

    private fun configureViews(view: View) {

        // Set title
        view.findViewById<TextView>(R.id.settings_title).text = contextDialog.resources.getString(R.string.settings_foods_text)

        // Configure table view
        configureRecyclerView(view)

        // Configure button SAVE and CANCEL
        configureButtons(view)
    }

    private fun configureButtons(view: View) {

        view.findViewById<Button>(R.id.button_save).setOnClickListener{

            // If at least one food changed, proceed to update
            if(listFoodChanged.isNotEmpty()){
                for(food in listFoodChanged){
                    updateFood(food, context = contextDialog)
                }
            }

            onFoodEventSettingsClick.showSnackBarSettingsSaved(contextDialog.resources.getString(R.string.settings_saved))

            dismiss()
        }

        view.findViewById<Button>(R.id.button_cancel).setOnClickListener{
            dismiss()
        }
    }

    private fun configureListFoodTypes(view: View){
        val title = view.findViewById<Spinner>(R.id.spinner_foodtype)

        // Spinner click listener
        val onItemSelected = object:AdapterView.OnItemSelectedListener  {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, itemView: View?, position: Int, id: Long) {
                foodTypeSelected = listFoodTypes[position]
                configureViews(view)
            }
        }

        title.onItemSelectedListener = onItemSelected

        fun getFoodTypesNamesFromListFoodTypes():ArrayList<String>{

            val result = arrayListOf<String>()

            if(listFoodTypes.isNotEmpty()){
                for(e in listFoodTypes){
                    val foodTypeName = e.name
                    result.add(foodTypeName)
                }
            }
            return result
        }

        foodTypeSelected = listFoodTypes[0]

        // Creating adapter for spinner
        val dataAdapter = ArrayAdapter(contextDialog, android.R.layout.simple_spinner_item, getFoodTypesNamesFromListFoodTypes())

        // Drop down layout style
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // attaching data adapter to spinner
        title.adapter = dataAdapter
    }

    private fun configureRecyclerView(view: View){

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        val onChangeStateForAnalysis = object : OnChangeStateForAnalysis {
            override fun changeStateForAnalysis(food: Food) {
                val foundFood = listFoodChanged.find { it.id == food.id}

                if(foundFood!=null){
                    listFoodChanged.remove(foundFood)
                } else {
                    listFoodChanged.add(food)
                }
            }
        }

        val mLayoutManager = LinearLayoutManager(contextDialog, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = mLayoutManager

        listFood.clear()
        listFoodChanged.clear()

        val listTemp = getListFoodByType(foodTypeSelected.id, context = contextDialog)

        if(listTemp!=null){
            listFood.addAll(0, listTemp)
            val foodSettingsAdapter = FoodSettingsAdapter(listFood, onChangeStateForAnalysis, contextDialog)
            recyclerView.adapter = foodSettingsAdapter
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextDialog = context
    }
}

class EventSettingsDialog : DialogFragment() {

    private lateinit var contextDialog: Context
    private lateinit var listEventTypes:List<EventType>
    private lateinit var onFoodEventSettingsClick:OnFoodEventSettingsClick

    fun newInstance(): EventSettingsDialog {
        return EventSettingsDialog()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.settings_layout, container)

        // init listEventTypesTemp
        val listEventTypesTemp = getAllEventTypes(false, contextDialog)

        // Configure title and table
        if(listEventTypesTemp!=null){
            listEventTypes = listEventTypesTemp

            if(activity is OnFoodEventSettingsClick)
                onFoodEventSettingsClick = activity as OnFoodEventSettingsClick

            configureViews(view)
        } else {
            Toast.makeText(contextDialog, contextDialog.resources.getString(R.string.no_eventtype_found), Toast.LENGTH_LONG).show()
            dismiss()
        }

        return view
    }

    private fun configureViews(view: View) {

        // Set title
        view.findViewById<TextView>(R.id.settings_title).text = contextDialog.resources.getString(R.string.settings_events_text)

        // Hide spinner layout
        view.findViewById<RelativeLayout>(R.id.layout_spinner).visibility = View.GONE

        // Hide columns headers
        view.findViewById<RelativeLayout>(R.id.layout_headers).visibility = View.GONE

        // Hide line separator
        view.findViewById<View>(R.id.line_separator).visibility = View.GONE

        // Configure table view
        configureRecyclerView(view)

        // Configure button SAVE and CANCEL
        configureButtons(view)
    }

    private fun configureButtons(view: View) {

        view.findViewById<Button>(R.id.button_save).visibility = View.GONE

        view.findViewById<Button>(R.id.button_cancel).text = contextDialog.resources.getString(R.string.quit)
        view.findViewById<Button>(R.id.button_cancel).setOnClickListener{
            dismiss()
        }
    }

    private fun configureRecyclerView(view: View){

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        val mLayoutManager = LinearLayoutManager(contextDialog, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = mLayoutManager

        val eventTypeSettingsAdapter = EventTypeSettingsAdapter(listEventTypes, contextDialog)
        recyclerView.adapter = eventTypeSettingsAdapter
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context!=null)
            contextDialog = context
    }
}


interface OnChangeStateForAnalysis {
    fun changeStateForAnalysis(food: Food)
}

interface OnFoodEventSettingsClick {
    fun showFoodSettingsDialog()

    fun showEventSettingsDialog()

    fun showSnackBarSettingsSaved(message:String)
}

