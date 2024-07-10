import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.parthfinder.R



@Composable
fun ImagePicker(context: Context) {
  var image by remember {
    mutableStateOf<ImageBitmap?>(null)
  }

  val pickImage = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
    uri?.let { image = uriToImageBitmap(context, it) }
  }

  Button(onClick = { pickImage.launch("image/*") }) {
    Text("Pick an image")
  }

  if(image != null){
    Image(bitmap = image!!, contentDescription = null)
  }
}

fun uriToImageBitmap(context: Context, uri: Uri): ImageBitmap {
  val inputStream = context.contentResolver.openInputStream(uri)
  val bitmap = BitmapFactory.decodeStream(inputStream)
  return bitmap.asImageBitmap()
}