db = db.getSiblingDB("users-db");

db.users.insertMany([
    {
        username: "aleksa",
        email: "velickovicaleksa555@gmail.com",
        firstName: "Aleksa",
        lastName: "Velickovic",
        passwordHash: "$2a$08$uPQ8FCcgcR2VqykhXdKW2OdVY9GIarOtVQJf4iTVur53aegBFA.A6",
        role: "USER",
        active: true,
        lastPasswordReset: new Date("2025-01-01T12:00:00Z")
    },
    {
        username: "testuser",
        email: "a.velickovic333@gmail.com",
        firstName: "Test",
        lastName: "User",
        passwordHash: "$2a$10$eImiTXuWVxfM37uY4JANjQ==",
        role: "USER",
        active: true,
        lastPasswordReset: new Date("2025-02-01T09:30:00Z")
    },
    {
        username: "admin",
        email: "aleksavelickovic555@gmail.com",
        firstName: "Admin",
        lastName: "Korisnik",
        passwordHash: "$2a$08$zkU3ED/TwsqjdBzMAJsLaumT.Lpy4Gky2KFXyE2.19dRh5WpYbSfO",
        role: "ADMIN",
        active: true,
        lastPasswordReset: new Date("2025-02-01T09:30:00Z")
    }
]);
