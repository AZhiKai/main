package seedu.address.logic.parser.menu;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Arrays;

import seedu.address.logic.commands.menu.FilterMenuCommand;
import seedu.address.logic.parser.Parser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.menu.TagContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FilterMenuCommand object
 */
public class FilterMenuCommandParser implements Parser<FilterMenuCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FilterMenuCommand
     * and returns an FilterMenuCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FilterMenuCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterMenuCommand.MESSAGE_USAGE));
        }

        String[] tags = trimmedArgs.split("\\s+");

        return new FilterMenuCommand(new TagContainsKeywordsPredicate(Arrays.asList(tags)));
    }

}
