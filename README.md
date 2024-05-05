# Welcome to RHIZOME

A implementation from scratch of Pandanite blockchain in java

## System environment

```
JDK: v21
Gradle: v8.5
```

## Develop

⚠️⚠️⚠️Rhizome is still in active development and not fonctionnal at this time⚠️⚠️⚠️


#### First stage: exploration

- [x] Implement core functions and objects

- [x] Add test cases: write test cases for existing functions

- [ ] Implement p2p layer optimized for low latency and fast sync

- [ ] Implement Basic blockcain services

- [ ] Implement the Pufferfish2 algorithm

- [ ] Define genesis of harfork

- [ ] Testnet

- [ ] Write documentation

#### Second stage: Launch

- [ ] Mainnet

- [ ] Expension and Stabilization of the network

- [ ] Optimize wallets to improve the user experience

- [ ] Official pool implementation


#### Third stage Expansion

- [ ] Support of smart contracts

- [ ] Development of use cases for defi

- [ ] Implement cross-chain protocols, compatible with access to multiple blockchain systems

- [ ] Bridge

## Code

- Git

  We use the gitflow branch model

  - `master` is the main branch, which is also used to deploy the production environment. Cannot modify the code directly at any time.
  - `develop` is the development branch, always keep the latest code after completion and bug fixes.
  - `feature` is a new feature branch. When developing new features, use the `develop` branch as the basis, and create the corresponding `feature/xxx` branch according to the development characteristics.
  - `release` is the pre-launch branch. During the release test phase, the release branch code will be used as the benchmark test. When a set of features is developed, it will be merged into the develop branch first, and a release branch will be created when entering the test. If there is a bug that needs to be fixed during the testing process, it will be directly fixed and submitted by the developer in the release branch. When the test is completed, merge the release branch to the master and develop branches. At this time, the master is the latest code and is used to go online.
  - `hotfix` is the branch for repairing urgent problems on the line. Using the `master` branch as the baseline, create a `hotfix/xxx` branch. After the repair is completed, it needs to be merged into the `master` branch and the `develop` branch.

- Commit Message

  The submission message must begin with a short subject line, followed by an optional, more detailed explanatory text, which should be separated from the abstract by a blank line.

- Pull Request

  The pull request must be as clear and detailed as possible, including all related issues. If the pull request is to close an issue, please use the Github keyword convention [`close`, `fix`, or `resolve`](https://help.github.com/articles/closing-issues-via-commit-messages/). If the pull request only completes part of the problem, use the `connected` keyword. This helps our tool to correctly link the issue to the pull request.
