import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime

@Composable
fun MessageBubble(sender: String, message: String, self: String) {


  var color by remember { mutableStateOf(Color.Gray) }
  var arrangement by remember { mutableStateOf(Arrangement.Start) }
  var startCorner by remember { mutableStateOf(0f) }
  var endCorner by remember { mutableStateOf(48f) }

  if (sender == self) {
    color = Color.Cyan
    arrangement = Arrangement.End
    startCorner = 48f
    endCorner = 0f
  }

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(5.dp),
    horizontalArrangement = arrangement
  ) {
    Box(
      modifier = Modifier
        .clip(
          RoundedCornerShape(
            topStart = 48f,
            topEnd = 48f,
            bottomEnd = endCorner,
            bottomStart = startCorner
          )
        )
        .background(color)
        .padding(10.dp)

    ) {
      Column {
        if (sender != self) {
          Text(text = sender, modifier = Modifier.padding(8.dp))
        }
        Text(text = message, modifier = Modifier.padding(8.dp))
      }
    }
  }
}
