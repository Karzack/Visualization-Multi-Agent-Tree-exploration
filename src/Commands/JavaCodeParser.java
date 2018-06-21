package Commands;


public final class JavaCodeParser {

    public static String parseCoder(String exp) {
        StringBuilder expression = new StringBuilder(exp);
        int locator = expression.length() - 1;
        String variables = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
        boolean forceInt= false;
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
                        forceInt=true;
                        break;
                    case "abs":
                        forceInt=true;
                        break;
                    case "ceil":
                        forceInt=true;
                        break;
                    case "sin":
                        forceInt=true;
                        break;
                    case "cos":
                        forceInt=true;
                        break;
                    case "log":
                        forceInt=true;
                        break;
                    case "sqrt":
                        forceInt=true;
                        break;
                    case "tan":
                        forceInt=true;
                        break;
                    case "round":
                        forceInt=true;
                        break;
                    default:
                        expression.delete(locator, locator + substringCalc);
                        expression.insert(locator, "vars.get(\"" + substring + "\")");
                        break;
                }
            }
            locator--;
        }
        if (forceInt){
            expression.insert(0,"(int)");
        }
        return expression.toString();

    }
}
