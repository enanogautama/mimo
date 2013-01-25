package binders;

import play.mvc.PathBindable;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;


public class DateW implements PathBindable<DateW> {

    public static DateW create(final Date date) {
        final DateW d = new DateW();
        d.date = date;
        return d;
    }

    @Override
    public DateW bind(String key, String txt) {
        Date result = null;
        try {
             result = simpleDateFormat.parse(txt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return DateW.create(result);
    }

    @Override
    public String unbind(String key) {
        return key + "=" + this.date.toString();
    }

    @Override
    public String javascriptUnbind() {
        return this.date.toString();
    }

    public Date getDate() {
        return this.date;
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Date date;
}