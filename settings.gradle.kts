rootProject.name = "sanos-y-salvos-backend"

include(
    ":apps:bff-service",
    ":services:user-service",
    ":services:report-service",
    ":services:geoservice",
    ":services:match-service",
    ":services:notification-service",
    ":services:collaboration-service",
    ":shared:common",
    ":shared:contracts",
)
