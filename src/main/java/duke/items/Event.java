package duke.items;

import duke.DukeException;
import java.lang.NumberFormatException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Event extends Item {
    private LocalDate time;
    private static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd yyyy");

    public Event(String name, String date) throws DukeException {
        super(name);
        String[] dateArgs = date.split("-");
        try {
            this.time = LocalDate.of(Integer.valueOf(dateArgs[0]),
                Integer.valueOf(dateArgs[1]), Integer.valueOf(dateArgs[2]));
        } catch (NumberFormatException | DateTimeException e) {
            throw new DukeException("please use format yyyy-mm-dd");
        }
    }

    @Override
    public String getPickle() {
        return super.getPickle() + "###" + this.time;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (at: " + this.time.format(dateFormat) + ")";
    }
}
