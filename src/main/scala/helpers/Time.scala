package helpers
import java.util.Calendar


trait Time {
  def getTime = Calendar.getInstance().getTime
}