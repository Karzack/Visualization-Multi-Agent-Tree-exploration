package Commands;


public final class JavaCodeParser {
    private static StringBuilder expression;
    private static String variables = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
    private static int locator;


    public static String parseCoder(String exp) {
        expression = new StringBuilder(exp);
        locator = expression.length() - 1;
        while (locator >=0 ) {
            if (variables.contains(String.valueOf(expression.charAt(locator)))) {
                int substringCalc = 1;
                boolean longVariable = true;
                do {
                    if (locator != 0) {
                        if (variables.contains(String.valueOf(expression.charAt(locator - 1)))) {
                            substringCalc++;
                            locator--;
                            if (locator == 0) {
                                longVariable = false;
                            }
                        } else {
                            longVariable = false;
                        }
                    } else {
                        longVariable = false;
                    }
                } while (longVariable);
                String substring = expression.substring(locator, locator + substringCalc);
                switch (substring) {
                    case "floor":
                        expression.insert(locator, "Math.");
                        break;
                    case "sin":
                        expression.insert(locator, "Math.");
                        break;
                    case "cos":
                        expression.insert(locator, "Math.");
                        break;
                    case "log":
                        expression.insert(locator, "Math.");
                        break;
                    default:
                        expression.delete(locator, locator + substringCalc);
                        expression.insert(locator, "vars.get(\"" + substring + "\")");
                        break;
                }
            }
            locator--;
        }
        return expression.toString();

    }
}
