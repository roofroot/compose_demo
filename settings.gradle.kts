pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            isAllowInsecureProtocol = true
            setUrl("http://nexus-iov.desaysv.com/repository/android-public/")
        }
        maven {
            isAllowInsecureProtocol = true
            setUrl("http://nexus3/repository/maven-public/")
        }
    }
}

rootProject.name = "compose_demo"
include(":app")
 