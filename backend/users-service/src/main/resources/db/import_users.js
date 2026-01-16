db = db.getSiblingDB("users-db");

db.users.insertMany([
    {
        username: "aleksa",
        email: "velickovicaleksa555@gmail.com",
        firstName: "Aleksa",
        lastName: "Velickovic",
        passwordHash: "$2y$10$OFPT0FcURNXGpHnVNKSu2Ox.7kkT1Ii0JjjBc/RVsiB17pLlfI2JK",
        role: "USER",
        active: true,
        lastPasswordReset: new Date("2025-01-01T12:00:00Z")
    },
    {
        username: "testuser",
        email: "a.velickovic333@gmail.com",
        firstName: "Test",
        lastName: "User",
        passwordHash: "$2y$10$L2ZvWUzS1iifI8Gh9NxDnuZU09GeClN9IfII68S/U/UrYQLHORiwm",
        role: "USER",
        active: true,
        lastPasswordReset: new Date("2025-02-01T09:30:00Z")
    },
    {
        username: "admin",
        email: "aleksavelickovic555@gmail.com",
        firstName: "Admin",
        lastName: "Korisnik",
        passwordHash: "$2y$10$FNSNzwxBvqfSlPgSdUbZvOmrUUtaXd0j7nOyfeMZElAWDfm/3ZxIy",
        role: "ADMIN",
        active: true,
        lastPasswordReset: new Date("2026-01-01T09:30:00Z")
    },
    {
        username: "adminold",
        email: "akica208@gmail.com",
        firstName: "Admin",
        lastName: "Korisnik",
        passwordHash: "$2y$10$FNSNzwxBvqfSlPgSdUbZvOmrUUtaXd0j7nOyfeMZElAWDfm/3ZxIy",
        role: "ADMIN",
        active: true,
        lastPasswordReset: new Date("2021-02-01T09:30:00Z")
    },
    {
        username: "milan",
        email: "maricmilan0414@gmail.com",
        firstName: "Admin",
        lastName: "Korisnik",
        passwordHash: "$2y$10$FNSNzwxBvqfSlPgSdUbZvOmrUUtaXd0j7nOyfeMZElAWDfm/3ZxIy",
        role: "ADMIN",
        active: true,
        lastPasswordReset: new Date("2025-02-01T09:30:00Z")
    }
]);
