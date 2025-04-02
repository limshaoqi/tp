package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ALLERGY;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ILLNESS;
import static seedu.address.logic.parser.CliSyntax.PREFIX_IMMUNIZATION;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NRIC;
import static seedu.address.logic.parser.CliSyntax.PREFIX_SURGERY;

import java.util.stream.Stream;

import seedu.address.logic.commands.AddMedicalReportCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.MedicalReport;
import seedu.address.model.person.Nric;

/**
 * Parses input arguments and creates a new AddMedicalReportCommand object
 */
public class AddMedicalReportCommandParser implements Parser<AddMedicalReportCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddMedicalReportCommand
     * and returns an AddMedicalReportCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public AddMedicalReportCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NRIC, PREFIX_ALLERGY, PREFIX_ILLNESS, PREFIX_SURGERY,
                        PREFIX_IMMUNIZATION);

        if (!arePrefixesPresent(argMultimap, PREFIX_NRIC)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    AddMedicalReportCommand.MESSAGE_USAGE));
        }

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NRIC, PREFIX_ALLERGY, PREFIX_ILLNESS,
                PREFIX_SURGERY, PREFIX_IMMUNIZATION);

        Nric nric = ParserUtil.parseNric(argMultimap.getValue(PREFIX_NRIC).get());
        String allergy = argMultimap.getValue(PREFIX_ALLERGY).orElse("None").trim();
        String illness = argMultimap.getValue(PREFIX_ILLNESS).orElse("None").trim();
        String surgery = argMultimap.getValue(PREFIX_SURGERY).orElse("None").trim();
        String immunization = argMultimap.getValue(PREFIX_IMMUNIZATION).orElse("None").trim();

        if (!isValidField(allergy) || !isValidField(illness)
                || !isValidField(surgery) || !isValidField(immunization)) {
            throw new ParseException(
                    "Invalid input! fields must contain only letters, numbers, spaces, commas, hyphens.");
        }

        MedicalReport medicalReport = new MedicalReport(allergy, illness, surgery, immunization);

        return new AddMedicalReportCommand(nric, medicalReport);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

    /**
     * Returns true if the field is non-empty, trimmed, and matches allowed characters.
     * Allowed: letters, numbers, spaces, commas, hyphens.
     */
    //Solution below inspired by:
    //https://stackoverflow.com/questions/22990870/how-to-disable-emoji-from-being-entered-in-android-edittext
    private boolean isValidField(String input) {
        return input != null
                && !input.trim().isEmpty()
                && input.matches("^[a-zA-Z0-9 ,\\-]+$") // Only letters, numbers, spaces, commas, hyphens
                && input.matches(".*[a-zA-Z].*"); // Must contain at least one alphabet
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof AddMedicalReportCommandParser;
    }
}
