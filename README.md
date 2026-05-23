Blackjack Console Game

A simple Blackjack game written in Java that runs entirely in the console.
The game includes a Monte Carlo Simulation based advisor that analyzes possible actions and displays their winning probabilities.

Features
Console-based Blackjack gameplay
Written in pure Java
Monte Carlo Simulation decision support system
Simulates 10,000 iterations for each possible action
Displays percentage chances for:
Hit
Stand
Double Down
Easy to run and modify
How It Works

The advisor algorithm uses Monte Carlo Simulation to estimate the probability of success for every available action.

For each move, the program runs 10,000 simulated games and calculates the success rate, helping the player make statistically better decisions.

Example output:

Your balance: $1000
Place your bet: $500

  DEALER  (showing: 9)

  Arif  (total: 10  |  balance: $500)

  Actions:  [H] Hit    [S] Stand    [D] Double Down    [?] Hint (3 left)
  Your choice: ?


    Stand:   23,7%                          
    Hit:     48,8%                          
    Double:  48,9%                          
    Best action: DOUBLE DOWN



Requirements
Java 8 or higher
