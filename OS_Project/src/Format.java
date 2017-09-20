/**
 * Created by bean on 4/14/2017.
 */
import java.io.PrintStream;

class Format {
    private int width = 0;
    private int precision = -1;
    private String pre = "";
    private String post = "";
    private boolean leading_zeroes = false;
    private boolean show_plus = false;
    private boolean alternate = false;
    private boolean show_space = false;
    private boolean left_align = false;
    private char fmt = 32;

    public Format(String var1) {
        boolean var2 = false;
        int var3 = var1.length();
        byte var4 = 0;

        int var5;
        for(var5 = 0; var4 == 0; ++var5) {
            if(var5 >= var3) {
                var4 = 5;
            } else if(var1.charAt(var5) == 37) {
                if(var5 >= var3 - 1) {
                    throw new IllegalArgumentException();
                }

                if(var1.charAt(var5 + 1) == 37) {
                    this.pre = this.pre + '%';
                    ++var5;
                } else {
                    var4 = 1;
                }
            } else {
                this.pre = this.pre + var1.charAt(var5);
            }
        }

        for(; var4 == 1; ++var5) {
            if(var5 >= var3) {
                var4 = 5;
            } else if(var1.charAt(var5) == 32) {
                this.show_space = true;
            } else if(var1.charAt(var5) == 45) {
                this.left_align = true;
            } else if(var1.charAt(var5) == 43) {
                this.show_plus = true;
            } else if(var1.charAt(var5) == 48) {
                this.leading_zeroes = true;
            } else if(var1.charAt(var5) == 35) {
                this.alternate = true;
            } else {
                var4 = 2;
                --var5;
            }
        }

        while(true) {
            while(var4 == 2) {
                if(var5 >= var3) {
                    var4 = 5;
                } else if(var1.charAt(var5) >= 48 && var1.charAt(var5) <= 57) {
                    this.width = this.width * 10 + var1.charAt(var5) - 48;
                    ++var5;
                } else if(var1.charAt(var5) == 46) {
                    var4 = 3;
                    this.precision = 0;
                    ++var5;
                } else {
                    var4 = 4;
                }
            }

            while(true) {
                while(var4 == 3) {
                    if(var5 >= var3) {
                        var4 = 5;
                    } else if(var1.charAt(var5) >= 48 && var1.charAt(var5) <= 57) {
                        this.precision = this.precision * 10 + var1.charAt(var5) - 48;
                        ++var5;
                    } else {
                        var4 = 4;
                    }
                }

                if(var4 == 4) {
                    if(var5 >= var3) {
                        boolean var6 = true;
                    } else {
                        this.fmt = var1.charAt(var5);
                    }

                    ++var5;
                }

                if(var5 < var3) {
                    this.post = var1.substring(var5, var3);
                }

                return;
            }
        }
    }

    public static double atof(String var0) {
        int var1 = 0;
        byte var2 = 1;
        double var3 = 0.0D;
        double var5 = 0.0D;
        double var7 = 1.0D;

        boolean var9;
        for(var9 = false; var1 < var0.length() && Character.isWhitespace(var0.charAt(var1)); ++var1) {
            ;
        }

        if(var1 < var0.length() && var0.charAt(var1) == 45) {
            var2 = -1;
            ++var1;
        } else if(var1 < var0.length() && var0.charAt(var1) == 43) {
            ++var1;
        }

        for(; var1 < var0.length(); ++var1) {
            char var10 = var0.charAt(var1);
            if(var10 >= 48 && var10 <= 57) {
                if(!var9) {
                    var3 = var3 * 10.0D + (double)var10 - 48.0D;
                } else if(var9) {
                    var7 /= 10.0D;
                    var3 += var7 * (double)(var10 - 48);
                }
            } else {
                if(var10 != 46) {
                    if(var10 != 101 && var10 != 69) {
                        return (double)var2 * var3;
                    }

                    long var11 = (long)((int)parseLong(var0.substring(var1 + 1), 10));
                    return (double)var2 * var3 * Math.pow(10.0D, (double)var11);
                }

                if(var9) {
                    return (double)var2 * var3;
                }

                var9 = true;
            }
        }

        return (double)var2 * var3;
    }

    public static int atoi(String var0) {
        return (int)atol(var0);
    }

    public static long atol(String var0) {
        int var1;
        for(var1 = 0; var1 < var0.length() && Character.isWhitespace(var0.charAt(var1)); ++var1) {
            ;
        }

        return var1 < var0.length() && var0.charAt(var1) == 48?(var1 + 1 >= var0.length() || var0.charAt(var1 + 1) != 120 && var0.charAt(var1 + 1) != 88?parseLong(var0, 8):parseLong(var0.substring(var1 + 2), 16)):parseLong(var0, 10);
    }

    private static String convert(long var0, int var2, int var3, String var4) {
        if(var0 == 0L) {
            return "0";
        } else {
            String var5;
            for(var5 = ""; var0 != 0L; var0 >>>= var2) {
                var5 = var4.charAt((int)(var0 & (long)var3)) + var5;
            }

            return var5;
        }
    }

    private String exp_format(double var1) {
        String var3 = "";
        int var4 = 0;
        double var5 = var1;

        double var7;
        for(var7 = 1.0D; var5 > 10.0D; var5 /= 10.0D) {
            ++var4;
            var7 /= 10.0D;
        }

        while(var5 < 1.0D) {
            --var4;
            var7 *= 10.0D;
            var5 *= 10.0D;
        }

        if((this.fmt == 103 || this.fmt == 71) && var4 >= -4 && var4 < this.precision) {
            return this.fixed_format(var1);
        } else {
            var1 *= var7;
            var3 = var3 + this.fixed_format(var1);
            if(this.fmt != 101 && this.fmt != 103) {
                var3 = var3 + "E";
            } else {
                var3 = var3 + "e";
            }

            String var9 = "000";
            if(var4 >= 0) {
                var3 = var3 + "+";
                var9 = var9 + var4;
            } else {
                var3 = var3 + "-";
                var9 = var9 + -var4;
            }

            return var3 + var9.substring(var9.length() - 3, var9.length());
        }
    }

    private String fixed_format(double var1) {
        String var3 = "";
        if(var1 > 9.223372036854776E18D) {
            return this.exp_format(var1);
        } else {
            long var4 = (long)(this.precision == 0?var1 + 0.5D:var1);
            var3 = var3 + var4;
            double var6 = var1 - (double)var4;
            return var6 < 1.0D && var6 >= 0.0D?var3 + this.frac_part(var6):this.exp_format(var1);
        }
    }

    public String form(char var1) {
        if(this.fmt != 99) {
            throw new IllegalArgumentException();
        } else {
            String var2 = String.valueOf(var1);
            return this.pad(var2);
        }
    }

    public String form(double var1) {
        if(this.precision < 0) {
            this.precision = 6;
        }

        byte var4 = 1;
        if(var1 < 0.0D) {
            var1 = -var1;
            var4 = -1;
        }

        String var3;
        if(this.fmt == 102) {
            var3 = this.fixed_format(var1);
        } else {
            if(this.fmt != 101 && this.fmt != 69 && this.fmt != 103 && this.fmt != 71) {
                throw new IllegalArgumentException();
            }

            var3 = this.exp_format(var1);
        }

        return this.pad(this.sign(var4, var3));
    }

    public String form(long var1) {
        byte var4 = 0;
        String var3;
        if(this.fmt != 100 && this.fmt != 105) {
            if(this.fmt == 111) {
                var3 = convert(var1, 3, 7, "01234567");
            } else if(this.fmt == 120) {
                var3 = convert(var1, 4, 15, "0123456789abcdef");
            } else {
                if(this.fmt != 88) {
                    throw new IllegalArgumentException();
                }

                var3 = convert(var1, 4, 15, "0123456789ABCDEF");
            }
        } else {
            var4 = 1;
            if(var1 < 0L) {
                var1 = -var1;
                var4 = -1;
            }

            var3 = String.valueOf(var1);
        }

        return this.pad(this.sign(var4, var3));
    }

    public String form(String var1) {
        if(this.fmt != 115) {
            throw new IllegalArgumentException();
        } else {
            if(this.precision >= 0) {
                var1 = var1.substring(0, this.precision);
            }

            return this.pad(var1);
        }
    }

    private String frac_part(double var1) {
        String var3 = "";
        if(this.precision > 0) {
            double var4 = 1.0D;
            String var6 = "";

            for(int var7 = 1; var7 <= this.precision && var4 <= 9.223372036854776E18D; ++var7) {
                var4 *= 10.0D;
                var6 = var6 + "0";
            }

            long var8 = (long)(var4 * var1 + 0.5D);
            var3 = var6 + var8;
            var3 = var3.substring(var3.length() - this.precision, var3.length());
        }

        if(this.precision > 0 || this.alternate) {
            var3 = "." + var3;
        }

        if((this.fmt == 71 || this.fmt == 103) && !this.alternate) {
            int var10;
            for(var10 = var3.length() - 1; var10 >= 0 && var3.charAt(var10) == 48; --var10) {
                ;
            }

            if(var10 >= 0 && var3.charAt(var10) == 46) {
                --var10;
            }

            var3 = var3.substring(0, var10 + 1);
        }

        return var3;
    }

    private String pad(String var1) {
        String var2 = repeat(' ', this.width - var1.length());
        return this.left_align?this.pre + var1 + var2 + this.post:this.pre + var2 + var1 + this.post;
    }

    private static long parseLong(String var0, int var1) {
        int var2 = 0;
        byte var3 = 1;

        long var4;
        for(var4 = 0L; var2 < var0.length() && Character.isWhitespace(var0.charAt(var2)); ++var2) {
            ;
        }

        if(var2 < var0.length() && var0.charAt(var2) == 45) {
            var3 = -1;
            ++var2;
        } else if(var2 < var0.length() && var0.charAt(var2) == 43) {
            ++var2;
        }

        for(; var2 < var0.length(); ++var2) {
            char var6 = var0.charAt(var2);
            if(var6 >= 48 && var6 < 48 + var1) {
                var4 = var4 * (long)var1 + (long)var6 - 48L;
            } else if(var6 >= 65 && var6 < 65 + var1 - 10) {
                var4 = var4 * (long)var1 + (long)var6 - 65L + 10L;
            } else {
                if(var6 < 97 || var6 >= 97 + var1 - 10) {
                    return var4 * (long)var3;
                }

                var4 = var4 * (long)var1 + (long)var6 - 97L + 10L;
            }
        }

        return var4 * (long)var3;
    }

    public static void print(PrintStream var0, String var1, char var2) {
        var0.print((new Format(var1)).form(var2));
    }

    public static void print(PrintStream var0, String var1, double var2) {
        var0.print((new Format(var1)).form(var2));
    }

    public static void print(PrintStream var0, String var1, long var2) {
        var0.print((new Format(var1)).form(var2));
    }

    public static void print(PrintStream var0, String var1, String var2) {
        var0.print((new Format(var1)).form(var2));
    }

    private static String repeat(char var0, int var1) {
        if(var1 <= 0) {
            return "";
        } else {
            StringBuffer var2 = new StringBuffer(var1);

            for(int var3 = 0; var3 < var1; ++var3) {
                var2.append(var0);
            }

            return var2.toString();
        }
    }

    private String sign(int var1, String var2) {
        String var3 = "";
        if(var1 < 0) {
            var3 = "-";
        } else if(var1 > 0) {
            if(this.show_plus) {
                var3 = "+";
            } else if(this.show_space) {
                var3 = " ";
            }
        } else if(this.fmt == 111 && this.alternate && var2.length() > 0 && var2.charAt(0) != 48) {
            var3 = "0";
        } else if(this.fmt == 120 && this.alternate) {
            var3 = "0x";
        } else if(this.fmt == 88 && this.alternate) {
            var3 = "0X";
        }

        int var4 = 0;
        if(this.leading_zeroes) {
            var4 = this.width;
        } else if((this.fmt == 100 || this.fmt == 105 || this.fmt == 120 || this.fmt == 88 || this.fmt == 111) && this.precision > 0) {
            var4 = this.precision;
        }

        return var3 + repeat('0', var4 - var3.length() - var2.length()) + var2;
    }
}
