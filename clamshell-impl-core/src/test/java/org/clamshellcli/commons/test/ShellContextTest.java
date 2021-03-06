/*
 * #%L
 * clamshell-commons
 * %%
 * Copyright (C) 2011 ClamShell-Cli
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.clamshellcli.commons.test;

import java.io.File;
import org.clamshellcli.api.Configurator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.clamshellcli.api.Context;
import org.clamshellcli.api.Plugin;
import org.clamshellcli.api.Shell;
import org.clamshellcli.core.Clamshell;
import org.clamshellcli.core.ShellContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author vvivien
 */
public class ShellContextTest {
    static {
        System.setProperty(Configurator.KEY_CONFIG_FILE, "../mock-env/conf/cli.config");
    }
    private ShellContext context;
    
    public ShellContextTest() {
        context = ShellContext.createInstance();
        try{
        ClassLoader parent = Clamshell.ClassManager.getClassLoaderFromFiles(
            new File[]{new File("../mock-env/plugins")}, 
            Pattern.compile(".*\\.jar"), 
            Thread.currentThread().getContextClassLoader()
        );
        List<Plugin> plugins = Clamshell.Runtime.loadServicePlugins(Plugin.class, parent);
        context.putValue(Context.KEY_PLUGINS, plugins);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    
    @Test
    public void testCreateContext() {
        ShellContext ctx = ShellContext.createInstance();
        assert ctx != null;
    }
    
    @Test
    public void testInsertSingleValues(){
        class Person {
            public String N = "Name";
            public String P = "Human";
        }
        context.putValue("A", "Abacore");
        context.putValue("B", "Bob the builder");
        context.putValue("C", new Person());
        
        assert context.getValue("A") != null : "ShellContext not saving values";
        assert context.getValue("B") instanceof String : "ShellContext not return data properly";
        Person p = (Person) context.getValue("C");
        assert p != null;
        assert p.N.equals("Name");
        assert p.P.equals("Human");
    }
    
    @Test
    public void testInsertBulkValues() {
        class Person {
            public String N = "Name";
            public String P = "Human";
        }
        Map<String, Object> a = new HashMap<String, Object>();
        a.put("A", "Abacore");
        a.put("B", "Bob the builder");
        a.put("C", new Person());
        
        context.putValues(a);
        assert context.getValue("A") != null : "ShellContext not saving values";
        assert context.getValue("B") instanceof String : "ShellContext not return data properly";
        Person p = (Person) context.getValue("C");
        assert p != null;
        assert p.N.equals("Name");
        assert p.P.equals("Human");        
    }
    
    @Test
    public void testGetConfigurator(){
        assert context.getConfigurator() != null;
    }
    
    @Test
    public void testGetPlugins() throws Exception{
        List<Plugin> plugins = context.getPlugins();
        assert plugins != null;
        assert plugins.size() == 5;
    }
    
    @Test
    public void testFilterPluginByType() {
        List<Shell> shells = context.getPluginsByType(Shell.class);
        assert shells != null;
        assert shells.size() == 1;
    }
    
    @Test
    public void testGetShell(){
        List<Shell> shells = context.getPluginsByType(Shell.class);
        assert !shells.isEmpty();
        context.putValue(Context.KEY_SHELL_COMPONENT, shells.get(0));
        Shell shell = context.getShell();
        assert shell != null;
    }

}
