rootProject.name = "sanos-y-salvos-backend"

include(
    ":apps:bff-service",
    ":services:user-service",
    ":services:pet-service",
    ":services:geoservice",
    ":services:match-service",
    ":shared:common",
    ":shared:contracts",
)
