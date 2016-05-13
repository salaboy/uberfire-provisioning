/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uberfire.provisioning.integration.tests;

import java.util.List;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.provisioning.build.maven.MavenBinary;
import org.uberfire.provisioning.build.maven.MavenBuild;
import org.uberfire.provisioning.build.maven.MavenProject;
import org.uberfire.provisioning.build.spi.Binary;
import org.uberfire.provisioning.build.spi.Build;
import org.uberfire.provisioning.build.spi.Project;
import org.uberfire.provisioning.build.spi.exceptions.BuildException;
import org.uberfire.provisioning.registry.BuildRegistry;
import org.uberfire.provisioning.registry.SourceRegistry;
import org.uberfire.provisioning.registry.local.InMemoryBuildRegistry;
import org.uberfire.provisioning.registry.local.InMemorySourceRegistry;
import org.uberfire.provisioning.source.Repository;
import org.uberfire.provisioning.source.Source;
import org.uberfire.provisioning.source.exceptions.SourcingException;
import org.uberfire.provisioning.source.github.GitHubRepository;
import org.uberfire.provisioning.source.github.GitSource;

/**
 *
 * @author salaboy
 */
@RunWith(Arquillian.class)
public class SimpleSourceAndBuildAPITest {

    @Deployment
    public static JavaArchive createDeployment() {

        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                .addClass(GitSource.class)
                .addClass(MavenBuild.class)
                .addClass(MavenProject.class)
                .addClass(InMemorySourceRegistry.class)
                .addClass(InMemoryBuildRegistry.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(jar.toString(true));
        return jar;
    }

    @Inject
    private SourceRegistry sourceRegistry;

    @Inject
    private BuildRegistry buildRegistry;

    @Inject
    private Source source;

    @Inject
    private Build build;

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {

    }

    @After
    public void tearDown() {
    }

    @Test
    public void helloSourceAndBuildAPIs() throws SourcingException, BuildException {

        Repository repo = new GitHubRepository("livespark playground");
        repo.setURI("https://github.com/pefernan/livespark-playground.git");
        String location = source.getSource(repo);
        System.out.println("Location : " + location);
        sourceRegistry.registerRepositorySources(location, repo);

        List<Repository> allRepositories = sourceRegistry.getAllRepositories();
        Assert.assertEquals(1, allRepositories.size());

        Project project = new MavenProject("users-new");
        project.setRootPath(location);
        project.setPath("users-new");
        project.setExpectedBinary("users-new.war");

        sourceRegistry.registerProject(repo, project);

        List<Project> projectsAll = sourceRegistry.getAllProjects(repo);
        Assert.assertEquals(1, projectsAll.size());
        
        List<Project> projectsByName = sourceRegistry.getProjectByName("users-new");
        Assert.assertEquals(1, projectsByName.size());
        
        int result = build.build(project);

        Assert.assertTrue(result == 0);
        System.out.println("Result: " + result);

        boolean binariesReady = build.binariesReady(project);
        Assert.assertTrue(binariesReady);

        System.out.println("binariesReady " + binariesReady);

        String finalLocation = build.binariesLocation(project);
        System.out.println("finalLocation " + finalLocation);

        MavenBinary mavenBinary = new MavenBinary(project);
        buildRegistry.registerBinary(mavenBinary);

        List<Binary> allBinaries = buildRegistry.getAllBinaries();
        Assert.assertEquals(1, allBinaries.size());

    }

}
