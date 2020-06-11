package Insomnia.Connection;

import java.util.*;
import java.util.regex.*;

/**
 * This class is provided to get the inputs from the user and parse them. It checks if the input
 * is valid and if so, it educes the information of the HTTP connection to be created such as its
 * URL, method, headers, etc.
 *
 * @author Negar Movaghatian
 */
public class InputHandler {

    private String[] args; // An array of the inputs to analyze

    private LinkedList<String> url; // The URL of the connection to be created
    private String method; // The method of the connection to be created
    private HashMap<String, String> headers; // The request headers
    private HashMap<String, String> formData; // The body of the request
    private boolean showResponseHeaders; // Shows if the user wants to see the response headers or not
    private boolean followRedirect; // Shows if the user wants this program to follow redirects automatically or not
    private String fileName; // The name of the output of this request
    private String binaryFilePath; // The path of the binary file to upload

    private boolean isMethod; // Shows if the next argument should be a method
    private boolean isHeader; // Shows if the next argument should be a list of headers
    private boolean isFileName; // Shows if the next argument should be a file name
    private boolean isData; // Shows if the next argument should be form data
    private boolean saveFile; // Shows if we should save this request or not
    private boolean uploadBinary; // Shows if the user wants to upload any binary file or not


    /**
     * Create a new input handler.
     */
    public InputHandler() {
        url = new LinkedList<>();
        headers = new HashMap<>();
        formData = new HashMap<>();
        method = "GET";
    }

    /**
     * Get input from the user and analyze the given arguments.
     * @return A string which shows the status of the input. whether it contains 'help' command, 'list' or 'fire' or is
     * an 'invalid input'. If none of the cases mentioned above, it will return 'none'.
     */
    public String getInput() {
        String input = "";
        for (int i=0; i<args.length; i++)
            input = input.concat(args[i] + " ");
        String parserResult = wholeInputParser(input);
        if (!parserResult.equals("none"))
            return parserResult;
        for (int i=0; i<args.length; i++)
            if (!inputParser(args[i], (i == args.length-1)))
                return "invalid input";
        return "new request";
    }

    /**
     * Search the whole input for more basic phrases like help, list or fire. these commands
     * have a higher priority.
     * @param input The input to search in.
     * @return The command found in the input. it can be 'help', 'list' or 'fire' and if not
     *         it will be 'none'.
     */
    private String wholeInputParser(String input) {
        if ((input.contains("-h") && (input.indexOf("-h")<1 || input.charAt(input.indexOf("-h")-1) == ' ')) ||
                (input.contains("--help") && (input.indexOf("--help")<1 || (input.charAt(input.indexOf("--help")-1)) == ' '))) {
            showHelp();
            return "help";
        }
        else if (input.contains("list") && ((input.indexOf("list")<1 || input.charAt(input.indexOf("list")-1) == ' '))) {
            RequestManager.showSavedRequests();
            return "list";
        }
        else if (input.contains("fire") && (input.indexOf("fire")<1 || input.charAt(input.indexOf("fire")-1) == ' ')) {
            Pattern headerPattern = Pattern.compile("fire( +(\\d+))+");
            Matcher matcher = headerPattern.matcher(input);
            if (!matcher.find()) {
                System.out.println("Invalid Expression");
                return "invalid input";
            }
            String[] fireNumbers = matcher.group(0).trim().split("\\s+");
            for (int i=1; i<fireNumbers.length; i++)
                RequestManager.runRequest(Integer.parseInt(fireNumbers[i]));
            return "fire";
        }
        return "none";
    }

    /**
     * Takes the last argument entered by the user and analyzes it.
     * @param input The last argument entered by the user.
     * @param isLastArgument Determine it this is the  last argument of all the arguments or not.
     * @return True if the command is valid and false otherwise.
     */
    private boolean inputParser (String input, boolean isLastArgument) {
        if (input.startsWith("-")) {
            if (!isValidArgument(input)) {
                System.out.println("No such an argument specifier as " + input);
                return false;
            }
            isMethod = false;
            isData = false;
        }
        if (isFileName && !input.startsWith("-")) {
            fileName = input;
            isFileName = false;
            return true;
        }
        else if (isFileName && input.startsWith("-")) {
            fileName = "output_" + System.currentTimeMillis();
            isFileName = false;
        }
        else if (uploadBinary && input.startsWith("-")) {
            System.out.println("Expected a file path");
            return false;
        }
        if (input.equals("-M") || input.equals("--method"))
            isMethod = true;
        else if (isMethod && !setMethod(input)) {
            System.out.println("Ambiguous command");
            return false;
        }
        else if (isMethod);
        else if (input.equals("--upload"))
            uploadBinary = true;
        else if (uploadBinary) {
            if (StreamUtils.isPathValid(input)) {
                uploadBinary = false;
                binaryFilePath = input;
            }
            else
                return false;
        }
        else if (input.equals("-d") || input.equals("--data"))
            isData = true;
        else if (isData) {
            if (!dataTokenizer(input))
                return false;
            isData = false;
        }
        else if (input.equals("-H") || input.equals("--headers"))
            isHeader = true;
        else if (isHeader) {
            if (!headerTokenizer(input))
                return false;
            isHeader = false;
        }
        else if (input.equals("-i"))
            showResponseHeaders = true;
        else if (input.equals("-O") || input.equals("--output")) {
            isFileName = true;
            if (isLastArgument)
                fileName = "output_" + System.currentTimeMillis();
        }
        else if (input.equals("-f"))
            followRedirect = true;
        else if (input.equals("-S") || input.equals("--save"))
            saveFile = true;
        else
            url.add(input);
        return true;
    }

    /**
     * Checks if the given list of headers is in the right format or not, if it is
     * it adds the given headers to a HashMap.
     * @param input the list of the headers to be checked.
     * @return True if the input is valid and false otherwise.
     */
    private boolean headerTokenizer(String input) {
        Pattern headerPattern = Pattern.compile("(([^:;]*):([^;:]*))(;([^;:]*):([^;:]*))*");
        Matcher matcher = headerPattern.matcher(input);
        if (!matcher.find()) {
            System.out.println("Invalid Expression");
            return false;
        }
        else {
            int i = 0;
            while (i<input.length()) {
                String name = "", value = "";
                while (input.charAt(i) != ':')
                    name = name.concat(input.charAt(i++) + "");
                i++;
                while (i<input.length() && input.charAt(i) != ';')
                    value = value.concat(input.charAt(i++) + "");
                i++;
                headers.put(name, value);
            }
        }
        return true;
    }

    /**
     * Checks if the given form data is in the right format or not, if it is
     * it adds the given data to a HashMap.
     * @param input the form data to be checked.
     * @return True if the input is valid and false otherwise.
     */
    private boolean dataTokenizer(String input) {
        Pattern headerPattern = Pattern.compile("(([^=&]*)=([^=&]*))(&([^=&]*)=([^=&]*))*");
        Matcher matcher = headerPattern.matcher(input);
        if (!matcher.find()) {
            System.out.println("Invalid Expression");
            return false;
        }
        else {
            int i = 0;
            while (i<input.length()) {
                String name = "", value = "";
                while (input.charAt(i) != '=')
                    name = name.concat(input.charAt(i++) + "");
                i++;
                while (i<input.length() &&  input.charAt(i) != '&')
                    value = value.concat(input.charAt(i++) + "");
                i++;
                formData.put(name, value);
            }
        }
        return true;
    }

    /**
     * Set the method of the request. Also checks if the given method is valid
     * or not.
     * @param method The name of the method entered by the user.
     * @return True if the method is a valid one and false otherwise.
     */
    private boolean setMethod(String method) {
        String[] methods = {"GET", "DELETE", "POST", "PUT", "PATCH"};
        boolean isMethodValid = false;
        for (String m : methods)
            if (method.equals(m))
                isMethodValid = true;
        if (!isMethodValid)
            return false;
        this.method = method;
        return true;
    }

    /**
     * Print the accepted arguments on stdout.
     */
    private void showHelp() {
        System.out.println("HELP:\n" +
                " -d, --data <data>          HTTP POST data\n" +
                " -f                         Follow redirects automatically\n" +
                " fire <request number>      Run the requests with the given order\n" +
                " -h, --help                 This help text\n" +
                " -H, --headers <header>     Pass custom header(s) to server\n" +
                " -i                         Include protocol response headers in the output\n" +
                " list                       List all the saved requests\n" +
                " -M, --method               Request method (Default: GET)\n" +
                " -O, --output <file>        Write to file instead of stdout\n" +
                " -S, --save                 Save this request\n" +
                " --upload <file path>       HTTP POST data\n");

    }

    /**
     * Check if the given argument specifier is valid or not.
     * @param arg The argument specifier to check.
     * @return True if the argument is valid and false otherwise.
     */
    private boolean isValidArgument (String arg) {
        String[] arguments = {"-M", "--method", "-H", "--headers", "-i", "-h", "--help", "-f",
                                "-O", "--output", "-S", "--save", "-d", "--d", "--upload"};
        for (String validArg : arguments)
            if (arg.equals(validArg))
                return true;
        return false;
    }

    /**
     * @return The list of the URLs found in the user's input.
     */
    public LinkedList<String> getUrl() {
        return url;
    }

    /**
     * @return The method found in the user's input.
     */
    public String getMethod() {
        return method;
    }

    /**
     * @return The form-data HashMap found in the user's input.
     */
    public HashMap<String, String> getFormData() {
        return formData;
    }

    /**
     * @return A boolean which shows if the user wants to see response headers or not.
     */
    public boolean ShowResponseHeaders() {
        return showResponseHeaders;
    }

    /**
     * @return A boolean which shows if the user wants the program to follow redirects
     * automatically or not.
     */
    public boolean isFollowRedirect() {
        return followRedirect;
    }

    /**
     * @return The output file name found in the user's input.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return The header HashMap found in the user's input.
     */
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * @return A boolean which shows if the user wants to save the request response or not.
     */
    public boolean hasFileName() {
        return !(fileName == null);
    }

    /**
     * @return A boolean which shows if the user wants to save this request or not.
     */
    public boolean isSaveFile() {
        return saveFile;
    }

    /**
     * @return The path of the binary file to upload found in the user's input.
     */
    public String getBinaryFilePath() {
        return binaryFilePath;
    }


    /**
     * @return A boolean which shows if the user wants to upload a binary file or not.
     */
    public boolean uploadBinary() {
        return !binaryFilePath.isEmpty();
    }

    /**
     * @param args The input arguments.
     */
    public void setArgs(String[] args) {
        this.args = args;
    }
}
