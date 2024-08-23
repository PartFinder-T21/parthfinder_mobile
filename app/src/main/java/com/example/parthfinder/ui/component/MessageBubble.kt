import android.content.Context
import androidx.compose.foundation.layout.Arrangement
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
fun MessageBubble(sender: String, message: String, timestamp: LocalDateTime, context: Context) {
  val self = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE).getString("name", null)

  var color by remember { mutableStateOf(Color.Gray) }
  var arrangement by remember { mutableStateOf(Arrangement.Start) }
  var startCorner by remember { mutableStateOf(0.dp) }
  var endCorner by remember { mutableStateOf(48.dp) }

  if (sender == self) {
    color = Color.Blue
    arrangement = Arrangement.End
    startCorner = 48.dp
    endCorner = 0.dp
  }

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = arrangement
  ) {
    Card(
      colors = CardDefaults.cardColors(containerColor = color),
      modifier = Modifier
        .clip(
          RoundedCornerShape(
            topStart = 48.dp,
            topEnd = 48.dp,
            bottomStart = startCorner,
            bottomEnd = endCorner
          )
        )

    ) {
      if (sender != self) {
        Text(text = sender, modifier = Modifier.padding(8.dp))
      }
      Text(text = message, modifier = Modifier.padding(8.dp))
    }
  }
}
