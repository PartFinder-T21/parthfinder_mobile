import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Dialog
import com.example.parthfinder.R
import com.example.parthfinder.api.AuthAPI
import com.example.parthfinder.api.Groups
import com.example.parthfinder.repository.Message

@Composable
fun ChatScreen(
  code: String,
  id: String,
  group: Groups,
  authAPI: AuthAPI,
  onExitPressed: () -> Unit
) {
  val context = LocalContext.current

  var messageList by remember {
    mutableStateOf(group.loadChatBy(code, authAPI, context).get())
  }



  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
      IconButton(
        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent),
        onClick = onExitPressed,
        content = {
          Icon(Icons.Rounded.Close, "Send")
        }
      )
    }
    ChatBox(authAPI = authAPI, messageList = messageList)
    MessageBox(id = id, group = group, authAPI = authAPI) {
      messageList = group.loadChatBy(code, authAPI, context).get()
    }
  }
}

@Composable
fun ChatBox(authAPI: AuthAPI, messageList: List<Message>) {
  val context = LocalContext.current

  val scrollState = rememberScrollState()
  val username = authAPI.getCookiesFromSharedPreferences(context)["name"]


  Box(
    modifier = Modifier
      .fillMaxSize(0.9f)
      .border(1.dp, Color.White)
  ) {
    Column(
      Modifier.verticalScroll(scrollState)
    ) {
      messageList.forEach {
        MessageBubble(
          sender = it.username ?: "",
          message = it.message ?: "",
          self = username ?: "other"
        )
      }
    }
  }
}

@Composable
fun MessageBox(id: String, group: Groups, authAPI: AuthAPI, updateChat: () -> Unit) {
  var chatBoxValue by remember { mutableStateOf("") }
  val context = LocalContext.current
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
    TextField(
      modifier = Modifier.clip(RoundedCornerShape(48f)),
      colors = TextFieldDefaults.colors(
        unfocusedContainerColor = Color.Gray,
        cursorColor = Color.Transparent
      ),
      value = chatBoxValue,
      onValueChange = { newText ->
        chatBoxValue = newText
        updateChat()
      },
      placeholder = {
        Text(text = "Type something")
      }
    )
    IconButton(
      modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(48f)),
      colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Gray),
      onClick = {
        group.sendMessageTo(id, chatBoxValue, authAPI, context)
        chatBoxValue = ""
        updateChat()
      },
      content = {
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.AutoMirrored.Rounded.Send, "Send")
          Text(text = "Send")
        }
      }
    )
  }
}

