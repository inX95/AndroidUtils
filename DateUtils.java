import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	
	private static final String TAG = "DateUtils";
	private static final String[] weekDays = {"星期天","星期一","星期二","星期三","星期四","星期五","星期六"};

	public static void addDay(Calendar calendar, int value) {
		calendar.add(Calendar.DATE, value);
	}

	public static void addMonth(Calendar calendar, int value) {
		calendar.add(Calendar.MONTH, value);
	}
	
	public static Calendar getWeekStartDate(Calendar calendar) {
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		Calendar calendar2 = (Calendar) calendar.clone();
		addDay(calendar2, 1 - dayOfWeek);
		return calendar2;
	}
	
	public static Calendar getWeekEndDate(Calendar calendar) {
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		Calendar calendar2 = (Calendar) calendar.clone();
		addDay(calendar2, 7 - dayOfWeek);
		return calendar2;
	}

	public static long getWeekStartTime(Calendar calendar) {
		Calendar calendar2 = getWeekStartDate(calendar);
		calendar2.set(Calendar.HOUR_OF_DAY, 0);
		calendar2.set(Calendar.MINUTE, 0);
		calendar2.set(Calendar.SECOND, 0);
		return calendar2.getTimeInMillis();
	}

	public static long getWeekEndTime(Calendar calendar) {
		Calendar calendar2 = getWeekEndDate(calendar);
		calendar2.set(Calendar.HOUR_OF_DAY, 23);
		calendar2.set(Calendar.MINUTE, 59);
		calendar2.set(Calendar.SECOND, 59);
		return calendar2.getTimeInMillis();
	}
	
	public static long getMonthStartTime(Calendar calendar) {
		Calendar calendar2 = (Calendar) calendar.clone();
		calendar2.set(Calendar.DAY_OF_MONTH, 1);
		return calendar2.getTimeInMillis();
	}
	
	public static long getMonthEndTime(Calendar calendar) {
		Calendar calendar2 = (Calendar) calendar.clone();
		addMonth(calendar2, 1);
		calendar2.set(Calendar.DAY_OF_MONTH, 1);
		return calendar2.getTimeInMillis() - 1;
	}
	
	public static float getWorkHour(String duration) {
		float second = Float.parseFloat(duration);
		float hour = second / 3600;
		BigDecimal b = new BigDecimal(hour);
		return b.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
	}

	public static String getWeekDay(Calendar calendar) {
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek < 1 || dayOfWeek > 7) {
			return "";
		}
		return weekDays[dayOfWeek - 1];
	}

	public static String getDate(long currentTime) {
		Date date = new Date(currentTime);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(date);
	}

	public static String getHour(long time) {
		return time / 3600 + "";
	}
	public static String getMinite(long time) {
		return Math.round(time % 3600 / (double) 60) + "";
	}

	public static String getTime(long currentTime) {
		//Website store the second of now, not the milisecond.
		Date date = new Date(currentTime * 1000);
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
		return formatter.format(date);
	}

}
