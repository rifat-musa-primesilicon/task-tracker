package com.r_mit.taskt

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.http.HttpException
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast
import androidx.annotation.RequiresExtension
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import android.net.Uri
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import android.Manifest
import android.graphics.Color
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    private lateinit var input1: EditText
    private lateinit var input2: EditText
    private lateinit var input3: EditText
    private lateinit var input4: Spinner
    private lateinit var input5: Spinner
    private lateinit var input6: EditText
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private lateinit var statusMessage: TextView

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }


        // Creation of notification channel for devices with Android O (API 26) and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "notification_channel"
            val channelName = "Reminder Notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Initialize the WebView
        webView = findViewById(R.id.webView)

        // OAuth Configuration
        val clientId = ""
        val redirectUri = "myapp://callback"
        val scope = "Files.ReadWrite User.Read"
        val authUrl = "https://login.microsoftonline.com/common/oauth2/v2.0/authorize?" +
                "client_id=$clientId&response_type=code&redirect_uri=$redirectUri&response_mode=query&scope=$scope"

        // Configure WebView
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.startsWith(redirectUri)) {
                    handleRedirect(url)
                    return true
                }
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d("WebView", "Loading URL: $url")
            }
        }

        // Load the OAuth URL
        webView.loadUrl(authUrl)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_notification, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.notification_off -> {
                Toast.makeText(this, "Notifications turned off", Toast.LENGTH_SHORT).show()
                cancelNotification() // Function to stop the notifications
                return true
            }
            R.id.notification_30min -> {
                Toast.makeText(this, "Notifications set to every 30 minutes", Toast.LENGTH_SHORT).show()
                scheduleNotification(30) // Function to schedule every 30 minutes
                return true
            }
            R.id.notification_1hour -> {
                Toast.makeText(this, "Notifications set to every hour", Toast.LENGTH_SHORT).show()
                scheduleNotification(60) // Function to schedule every hour
                return true
            }
            R.id.add_topic -> {
                // Show dialog to add a new topic
                showAddTopicDialog()
                true
            }
            R.id.delete_topic -> {
                // Show dialog to delete an existing topic
                showDeleteTopicDialog()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    private fun updateExcelWithUserInput(accessToken: String, values: List<String>) {
        val accessToken = accessToken
        val fileId = ""

        // Find the next empty row in Column A
        fetchColumnA(accessToken, fileId, values)
    }

    // Function to fetch data from Excel (kept for reference/testing)
    private fun fetchExcelData(accessToken: String, fileId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getExcelData(
                    fileId = fileId,
                    authHeader = "Bearer $accessToken"
                )
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Data fetched successfully!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    private fun fetchColumnA(accessToken: String, fileId: String, values: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.apiService.getExcelData(
                    fileId = fileId,
                    authHeader = "Bearer $accessToken"
                )

                // Get the values from column A (assuming the response is a list of rows)
                val columnAValues = response.values

                // Find the first empty cell in column A
                var nextEmptyRow = 1 // Start from A1
                for (i in columnAValues.indices) {
                    // Safely check if the row is empty or the first element is empty or null
                    val firstElement = columnAValues[i].getOrNull(0)?.toString() // Safe access to the first element

                    // Check if the first element is null or empty
                    if (firstElement.isNullOrEmpty()) {
                        nextEmptyRow = i + 1 // First empty row
                        break
                    }
                }

                // After fetching the data, update the Excel sheet at the next empty row
                updateExcelData(accessToken, fileId, "Sheet1", nextEmptyRow, values)

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Function to update the Excel file at the next available row
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    private fun updateExcelData(accessToken: String, fileId: String, sheetName: String, nextEmptyRow: Int, values: List<String>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val requestBody = UpdateRequestBody(listOf(values))
                val rangeAddress = "A$nextEmptyRow:F$nextEmptyRow"

                val totalRows = values.size
                // Track progress
//                withContext(Dispatchers.Main) {
//                    var progress = 0
//                    val progressBar: ProgressBar = findViewById(R.id.progressBar)
//                    val statusMessage: TextView = findViewById(R.id.statusMessage)
//
//                    // Make the progress bar and status message visible
//                    progressBar.visibility = View.VISIBLE
//                    statusMessage.visibility = View.VISIBLE
//
//                    // Update the progress
//                    for (i in 1..totalRows) {
//                        progress = (i * 100) / totalRows
//                        progressBar.progress = progress
//                        statusMessage.text = "Uploading: $progress%"
//
//                        // Add a small delay to simulate progress (you can replace this with real data sending logic)
//                        delay(200) // Adjust based on your actual upload time
//                    }
//                    progressBar.visibility = View.GONE
//                    statusMessage.visibility = View.GONE
//                }

                // Call the API to update the data at the calculated range
                RetrofitClient.apiService.updateExcelData(
                    fileId = fileId,
                    sheetName = sheetName,
                    rangeAddress = rangeAddress,
                    authHeader = "Bearer $accessToken",
                    requestBody = requestBody
                )

                withContext(Dispatchers.Main) {
                    var progress = 0
                    progressBar = findViewById(R.id.progressBar)
                    statusMessage = findViewById(R.id.statusMessage)

                    // Make the progress bar and status message visible
                    progressBar.visibility = View.VISIBLE
                    statusMessage.visibility = View.VISIBLE

                    // Update the progress
                    for (i in 1..totalRows) {
                        progress = (i * 100) / totalRows
                        progressBar.progress = progress
                        statusMessage.text = "Uploading: $progress%"

                        // Add a small delay to simulate progress (you can replace this with real data sending logic)
                        delay(200) // Adjust based on your actual upload time
                    }
                    Toast.makeText(
                        this@MainActivity,
                        "Excel sheet updated successfully!",
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar.visibility = View.GONE
                    statusMessage.visibility = View.GONE
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    if (e is retrofit2.HttpException) {
                        // Log the full response body for HTTP 400 error
                        try {
                            val responseBody = e.response()?.errorBody()?.string()
                            Log.e("APIError", "HTTP Error 400: ${e.message}")
                            Log.e("APIError", "Response Body: $responseBody") // Log the response body for debugging

                            // Optionally, handle the response body here (e.g., show user-friendly error message)
                            Toast.makeText(this@MainActivity, "Error: ${responseBody ?: e.message}", Toast.LENGTH_LONG).show()
                        } catch (ex: Exception) {
                            Log.e("APIError", "Error parsing response body: ${ex.message}")
                        }
                    } else {
                        // Handle non-HTTP errors
                        Log.e("Error", "Error: ${e.message}")
                        Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }

//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    if (e is HttpException) {
//                        // Handle API errors
//                        Toast.makeText(this@MainActivity, "API Error: ${e.message}", Toast.LENGTH_LONG).show()
//                    } else {
//                        // Handle other errors
//                        Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
        }
    }

    private fun handleRedirect(url: String) {
        val uri = Uri.parse(url)
        val authCode = uri.getQueryParameter("code")
        if (authCode != null) {
            android.util.Log.d("OAuth", "Authorization Code: $authCode")
            //Toast.makeText(this, "Authorization Code: $authCode", Toast.LENGTH_LONG).show()
            webView.stopLoading()  // Stops the WebView from further loading
            webView.loadUrl("about:blank")  // Clears the current webpage
            webView.clearHistory()  // Clears the browsing history
            webView.visibility = View.GONE  // Hides the WebView from view
            exchangeCodeForToken(authCode)
        } else {
            val error = uri.getQueryParameter("error")
            android.util.Log.e("OAuth", "Error during authorization: $error")
            //Toast.makeText(this, "Error: $error", Toast.LENGTH_LONG).show()
        }
    }

    private fun addTopic(topic: String) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val customTopics = sharedPreferences.getStringSet("topics", emptySet()) ?: emptySet()

        if (customTopics.contains(topic) || resources.getStringArray(R.array.input5_options).contains(topic)) {
            Toast.makeText(this, "Topic already exists", Toast.LENGTH_SHORT).show()
        } else {
            val updatedTopics = customTopics.toMutableSet().apply { add(topic) }
            sharedPreferences.edit().putStringSet("topics", updatedTopics).apply()
            updateSpinnerTopicsWithHint()
            Toast.makeText(this, "Topic added: $topic", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteTopic(topic: String) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val customTopics = sharedPreferences.getStringSet("topics", emptySet()) ?: emptySet()

        if (customTopics.contains(topic)) {
            val updatedTopics = customTopics.toMutableSet().apply { remove(topic) }
            sharedPreferences.edit().putStringSet("topics", updatedTopics).apply()
            updateSpinnerTopicsWithHint()
            Toast.makeText(this, "Topic deleted: $topic", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Topic not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadCustomTopics(): Set<String> {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return sharedPreferences.getStringSet("topics", emptySet()) ?: emptySet()
    }

    private fun loadTopics(): List<String> {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val predefinedTopics = resources.getStringArray(R.array.input5_options).toList()
        val customTopics = sharedPreferences.getStringSet("topics", emptySet()) ?: emptySet()

        // Combine predefined topics and custom topics, ensuring no duplicates
        return (predefinedTopics + customTopics).distinct()
    }

    private fun updateSpinnerTopicsWithHint() {
        val topics = loadTopics() // Load predefined and custom topics
        val allTopics = mutableListOf("Topic") // Add the hint
        allTopics.addAll(topics)

        val spinner = findViewById<Spinner>(R.id.input5)

        // Clear old adapter data to avoid duplication
        val adapter = object : ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            allTopics.distinct() // Ensure final list has no duplicates
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)

                // Set the text color for the selected item
                textView.setTextColor(Color.BLACK) // Ensure the selected item is black
                return view
            }

            override fun isEnabled(position: Int): Boolean {
                return position != 0 // Disable the first item ("Topic")
            }

            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getDropDownView(position, convertView, parent)
                val textView = view as TextView
                textView.setTextColor(if (position == 0) Color.WHITE else Color.WHITE)
                return view
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set the default selection to hint
        spinner.setSelection(0)
    }

    private fun showAddTopicDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Topic")

        val input = EditText(this)
        input.hint = "Enter new topic"
        builder.setView(input)

        builder.setPositiveButton("Add") { dialog, _ ->
            val newTopic = input.text.toString().trim()
            if (newTopic.isNotBlank()) {
                addTopic(newTopic)
            } else {
                Toast.makeText(this, "Topic cannot be empty", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun showDeleteTopicDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Topic")

        val topics = loadCustomTopics().toList()
        if (topics.isEmpty()) {
            Toast.makeText(this, "No topics to delete", Toast.LENGTH_SHORT).show()
            return
        }

        builder.setItems(topics.toTypedArray()) { dialog, which ->
            val topicToDelete = topics[which]
            deleteTopic(topicToDelete)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }



    private fun exchangeCodeForToken(authCode: String) {
        val client = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val requestBody = FormBody.Builder()
            .add("client_id", "")
            .add("redirect_uri", "myapp://callback")
            .add("grant_type", "authorization_code")
            .add("code", authCode)
            .build()

        val request = Request.Builder()
            .url("https://login.microsoftonline.com/common/oauth2/v2.0/token")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    //Toast.makeText(this@MainActivity, "Failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }

            @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody ?: "{}")
                    val accessToken = jsonObject.optString("access_token", "No Access Token")
                    // Store token securely
                    val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("ACCESS_TOKEN", accessToken)
                    editor.apply()
                    runOnUiThread {
                        //Toast.makeText(this@MainActivity, "Access Token: $accessToken", Toast.LENGTH_LONG).show()
                        input1 = findViewById(R.id.input1)
                        // Fetch current date (compatible with all Android versions)
                        val currentDate = SimpleDateFormat(
                            "EEE, MMM dd, yyyy",
                            Locale.getDefault()
                        ).format(Date())
                        input1.setText(currentDate) // Set the date in the input field
                        // Make the field visible but not editable
                        input1.isFocusable = false
                        input1.isClickable = false

                        input2 = findViewById(R.id.input2)
                        input3 = findViewById(R.id.input3)
                        // Get current time
                        val calendar = Calendar.getInstance()
                        val currentHour = calendar.get(Calendar.HOUR_OF_DAY) // 24-hour format
                        val currentMinute = calendar.get(Calendar.MINUTE)

                        // Calculate previous half-hour
                        val previousHalfHour: String = if (currentMinute < 30) {
                            String.format("%02d:00", currentHour) // e.g., 14:00
                        } else {
                            String.format("%02d:30", currentHour) // e.g., 14:30
                        }

                        // Calculate next half-hour
                        val nextHalfHour: String = if (currentMinute < 30) {
                            String.format("%02d:30", currentHour) // e.g., 14:30
                        } else {
                            String.format(
                                "%02d:00",
                                (currentHour + 1) % 24
                            ) // e.g., 15:00, handle overflow
                        }

                        // Set values
                        input2.setText(previousHalfHour)
                        input3.setText(nextHalfHour)

                        // Make fields non-editable
                        //input2.isFocusable = false
                        //input2.isClickable = false
                        //input3.isFocusable = false
                        //input3.isClickable = false
                        val input4Options = resources.getStringArray(R.array.input4_options)
                        input4 = findViewById(R.id.input4)

                        val adapter = object : ArrayAdapter<String>(
                            this@MainActivity,
                            R.layout.spinner_item,
                            input4Options
                        ) {
                            override fun isEnabled(position: Int): Boolean {
                                // Disable the placeholder (first position)
                                return position != 0
                            }

                            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                                val view = super.getDropDownView(position, convertView, parent)
                                val textView = view as TextView
                                // Set text color for disabled items
                                textView.setTextColor(if (position == 0) Color.WHITE else Color.WHITE)
                                return view
                            }
                        }

                        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
                        input4.adapter = adapter
                        input4.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                                if (position == 0) {
                                    //Toast.makeText(this@MainActivity, "Please select a valid category", Toast.LENGTH_SHORT).show()
                                } else {
                                    val selectedItem = input4Options[position]
                                    //Toast.makeText(this@MainActivity, "Selected: $selectedItem", Toast.LENGTH_SHORT).show()
                                }
                            }
                            override fun onNothingSelected(parent: AdapterView<*>) {}
                        }


                        input5 = findViewById<Spinner>(R.id.input5)
                        val predefinedCategories = resources.getStringArray(R.array.input5_options) // Load predefined categories from strings.xml
                        val input5Adapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_spinner_item, predefinedCategories.toMutableList())
                        input5Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        input5.adapter = input5Adapter
                        updateSpinnerTopicsWithHint()

                        input6 = findViewById(R.id.input6)
                        input6.setTextColor(Color.BLACK)

                            val submitButton: Button = findViewById(R.id.submit_button)
                            submitButton.setOnClickListener {
                                val values = listOf(
                                    input1.text.toString(),
                                    input2.text.toString(),
                                    input3.text.toString(),
                                    input4.selectedItem.toString(),
                                    input5.selectedItem.toString(),
                                    input6.text.toString(),
                                )
                                updateExcelWithUserInput(accessToken, values)
                            }
                    }
                } else {
                    runOnUiThread {
                        when (response.code) {
                            401 -> Toast.makeText(this@MainActivity, "Unauthorized: Invalid credentials", Toast.LENGTH_LONG).show()
                            500 -> Toast.makeText(this@MainActivity, "Server error. Please try again later.", Toast.LENGTH_LONG).show()
                            else -> Toast.makeText(this@MainActivity, "Unexpected error: ${response.code}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        })
    }

    private fun scheduleNotification(intervalMinutes: Long) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val startTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.timeInMillis

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            startTime,
            intervalMinutes * 60 * 1000,
            pendingIntent
        )
    }

    private fun cancelNotification() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}

