pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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
            setUrl("https://maven.innovatrics.com/releases")
        }
        //1.11.0
        // relative path to local directory with DOT and SFE Toolkit
        maven {
            setUrl("repo")
        }
    }
}

rootProject.name = "TruCrowdExample"
include(":app")
 