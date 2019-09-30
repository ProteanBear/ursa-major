package xyz.proteanbear.phecda.generator;

/**
 * Code generator for the data handler of the MyBatis
 *
 * @author ProteanBear
 * @version 1.0.0,2018/05/09
 * @since 1.0
 */
public class CodeGenerator
{
    /**
     * @param args arg1:tablePattern - Table name filter pattern setting
     *             arg2:package - The package location of the generated code
     *             arg3:withJoin - If the content of the associated table is generated, set to true
     *             arg4:coupling - Whether to use a coupling code, that is, direct association between multiple data
     *             arg5:foreignTable - Specify a foreign key association table, separated by commas
     */
    public static void main(String[] args)
    {
        //Load the args
        String foreignTable=args.length>4?args[4]:null;
        boolean coupling=args.length>3 && Boolean.parseBoolean(args[3]);
        boolean withJoin=args.length<=2 || Boolean.parseBoolean(args[2]);
        String rootPackage=args.length>1?args[1]:"xyz.proteanbear.business";
        String tablePattern=args.length>0?args[0]:".*";
        System.out.println("Start generating the mybatis class for a pattern '"+tablePattern+"' into a package '"+rootPackage+"'.(coupling:"+coupling+";withJoin:"+withJoin+"+;foreignTable:"+(foreignTable==null?"all":foreignTable)+")!");

        //Get current project location
        String projectPath=System.getProperty("user.dir");
        System.out.println("Get current project path:"+projectPath);

        //Load the database connection

    }
}