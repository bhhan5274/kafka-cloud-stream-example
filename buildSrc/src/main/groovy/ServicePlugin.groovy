import org.gradle.api.Plugin
import org.gradle.api.Project

class ServicePlugin implements Plugin<Project> {
    @Override
    void apply(Project project){
        project.apply(plugin: "org.springframework.boot")
        project.apply(plugin: "io.spring.dependency-management")

        project.dependencyManagement {
            imports {
                mavenBom "org.springframework.cloud:spring-cloud-dependencies:${project.ext.springCloudVersion}"
            }
        }
    }
}