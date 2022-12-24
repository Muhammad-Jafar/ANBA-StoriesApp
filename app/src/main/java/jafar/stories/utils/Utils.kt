package jafar.stories.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jafar.stories.R
import jafar.stories.databinding.CustomLoadingBinding
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/* TIME FORMAT */
private const val timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
private fun getCurrentDate(): Date = Date()
private fun getSimpleDate(date: Date): String = timeStamp.format(date)

fun getUploadStoryTime(timestamp: String): String {
    val date: Date = parseUTCDate(timestamp)
    return getSimpleDate(date)
}
fun parseUTCDate(timestamp: String): Date {
    return try {
        val formatter = SimpleDateFormat(timestampFormat, Locale.getDefault())
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        formatter.parse(timestamp) as Date
    } catch (e: ParseException) {
        getCurrentDate()
    }
}

fun getTimelineUpload(context: Context, timestamp: String): String {
    val currentTime = getCurrentDate()
    val uploadTime = parseUTCDate(timestamp)
    val diff: Long = currentTime.time - uploadTime.time
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    val label = when (minutes.toInt()) {
        0 -> "$seconds ${context.getString(R.string.second_ago)}"
        in 1..59 -> "$minutes ${context.getString(R.string.minutes_ago)}"
        in 60..1440 -> "$hours ${context.getString(R.string.hours_ago)}"
        else -> "$days ${context.getString(R.string.days_ago)}"
    }
    return label
}

fun formatDate(currentDateString: String, targetTimeZone: String): String {
    val instant = Instant.parse(currentDateString)
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy | HH:mm")
        .withZone(ZoneId.of(targetTimeZone))
    return formatter.format(instant)
}

/* CUSTOM ALERT DIALOG */
fun showAlertLoading(context: Context): AlertDialog {
    val binding = CustomLoadingBinding.inflate(LayoutInflater.from(context), null, false)
    return MaterialAlertDialogBuilder(context, R.style.CustomDialogLoading).setView(binding.root)
        .setCancelable(false).create()
}

/*HIDE SOFT KEYBOARD*/
fun hideSoftKeyboard(context: Context, view: View) {
    (context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(view.windowToken, 0)
}

/*CUSTOM UPLOAD FILE*/
private const val FILENAME_FORMAT = "dd-MMM-yyyy"
@SuppressLint("ConstantLocale")
val timeStamp: String =
    SimpleDateFormat(FILENAME_FORMAT, Locale.getDefault()).format(System.currentTimeMillis())

fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

/* PARSE ADDRESS LOCATION */
fun parseAddressLocation(
    context: Context,
    lat: Double,
    lon: Double
): String {
    val geocoder = Geocoder(context)
    val geoLocation = geocoder.getFromLocation(lat, lon, 1)
    return if (geoLocation.size > 0) {
        val location = geoLocation[0]
        val fullAddress = location.getAddressLine(0)
        StringBuilder("").append(fullAddress).toString()
    } else "📌 Location Unknown"
}