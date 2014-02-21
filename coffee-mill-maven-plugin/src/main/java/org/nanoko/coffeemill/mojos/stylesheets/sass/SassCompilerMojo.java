package org.nanoko.coffeemill.mojos.stylesheets.sass;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.nanoko.maven.WatchingException;
import org.nanoko.coffeemill.mojos.AbstractCoffeeMillWatcherMojo;
import org.nanoko.coffeemill.utils.FSUtils;
import org.nanoko.coffeemill.utils.MavenLoggerWrapper;
import org.nanoko.java.NPM;


import java.io.File;
import java.util.Collection;

import static org.nanoko.java.NPM.npm;

/**
 * Compiles less files.
 */
@Mojo(name = "compile-sass", threadSafe = false,
requiresDependencyResolution = ResolutionScope.COMPILE,
requiresProject = true,
defaultPhase = LifecyclePhase.COMPILE)
public class SassCompilerMojo extends AbstractCoffeeMillWatcherMojo {

    public static final String SASS_NPM_NAME = "node-sass";
    public static final String SASS_NPM_VERSION = "0.8.1";

    private NPM sass;


    public void execute() throws MojoExecutionException {    	
        if(isSkipped()) { 
            return; 
        }

        sass = npm(new MavenLoggerWrapper(this.getLog()), SASS_NPM_NAME, SASS_NPM_VERSION);
        try {
            if ( getStylesheetsDir().isDirectory()) {
                getLog().info("Compiling sass files from " + getStylesheetsDir().getAbsolutePath());
                Collection<File> files = FileUtils.listFiles(getStylesheetsDir(), new String[]{"scss"}, true);
                for (File file : files) {
                    if (file.isFile()) {
                        compile(file);
                    }
                }
            }
        } catch (WatchingException e) {
            throw new MojoExecutionException("Error during execute() on SassCompilerMojo : cannot compile", e);
        }
    }


    public boolean accept(File file) {
        return !isSkipped() 
                //&& FSUtils.isInDirectory(file.getName(), getStylesheetsDir())
                && file.getParent().contains( getStylesheetsDir().getAbsolutePath() )
                && FSUtils.hasExtension(file, "scss");
    }

    public boolean fileCreated(File file) throws WatchingException {
        compile(file);
        return true;
    }

    public boolean fileUpdated(File file) throws WatchingException {
        compile(file);
        return true;
    }

    public boolean fileDeleted(File file) {
        File theFile = getOutputCSSFile(file);
        FileUtils.deleteQuietly(theFile);
        return true;
    }



    private File getOutputCSSFile(File input) {
        String cssFileName = input.getName().substring(0, input.getName().length() - ".scss".length()) + ".css";
        String path = input.getParentFile().getAbsolutePath().substring(getStylesheetsDir().getAbsolutePath().length());
        return new File(this.getWorkDirectory(), path + "/" + cssFileName);
    }

    private void compile(File file) throws WatchingException {
        File out = getOutputCSSFile(file);
        getLog().info("Compiling " + file.getAbsolutePath() + " to " + out.getAbsolutePath());
        int exit = sass.execute("node-sass", file.getAbsolutePath(), out.getAbsolutePath());
        getLog().debug("Sass execution exiting with " + exit + " status");

        if (!out.isFile()) {
            throw new WatchingException("Error during the compilation of " + file.getAbsoluteFile() + " check log");
        }
    }

    private boolean isSkipped(){
        if (skipCssCompilation) {
            getLog().info("\033[31m Sass Compilation skipped \033[37m");
            return true;
        } else {
            return false;
        }
    }

}
