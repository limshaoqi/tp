package seedu.address.storage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.appointment.Appointment;

/**
 * Jackson-friendly version of {@link Appointment}.
 */
public class JsonAdaptedAppointment {
    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Appointment's %s field is missing!";

    private final String doctorNric;
    private final String appointmentDescription;
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * Constructs a {@code JsonAdaptedAppointment} with the given appointment details.
     */
    public JsonAdaptedAppointment(@JsonProperty("doctorNRIC") String doctorNric,
                                  @JsonProperty("appointmentDescription") String appointmentDescription,
                                    @JsonProperty("startDate") LocalDate startDate,
                                    @JsonProperty("endDate") LocalDate endDate) {
        this.doctorNric = doctorNric;
        this.appointmentDescription = appointmentDescription;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Converts a given {@code Appointment} into this class for Jackson use.
     */
    public JsonAdaptedAppointment(Appointment source) {
        doctorNric = source.getDoctorNric();
        appointmentDescription = source.getDescription();
        startDate = source.getStartDate();
        endDate = source.getEndDate();
    }

    /**
     * Converts this Jackson-friendly adapted appointment object into the model's {@code Appointment} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted appointment.
     */
    public Appointment toModelType() throws IllegalValueException, DateTimeParseException {
        if (doctorNric == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, "doctor nric"));
        }

        if (appointmentDescription == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, "appointment description"));
        }

        if (startDate == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, "start date"));
        }

        if (endDate == null) {
            throw new IllegalValueException(
                    String.format(MISSING_FIELD_MESSAGE_FORMAT, "end date"));
        }

        return new Appointment(doctorNric, appointmentDescription, startDate, endDate);
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof JsonAdaptedAppointment // instanceof handles nulls
                && doctorNric.equals(((JsonAdaptedAppointment) other).doctorNric)
                && appointmentDescription.equals(((JsonAdaptedAppointment) other).appointmentDescription)
                && startDate.equals(((JsonAdaptedAppointment) other).startDate)
                && endDate.equals(((JsonAdaptedAppointment) other).endDate));
    }
}

