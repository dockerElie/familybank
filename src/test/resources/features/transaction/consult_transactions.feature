Feature: consult list of transaction

  Rule: Access information related to the deposit with historical details

    Scenario: Access information related to the deposit is available only if transaction's status is created or validated
      Given account manager cancelled a transaction
      When account manager consults the list of transactions
      Then the list does not display cancelled transactions

    Scenario: no deposit has been reactivated
      Given account manager selected one transaction from the list (status = created or status = validated)
      When account manager consults deposit's details
      Then the deposit details is displayed in descending order based on the validate's date with following informations
        | Identifier  | Activated date | Name                     | Status | User's expiration date | Expiration date | Amount      | Description              |
        | 01-DEP-2022 | 12/08/2022     | Raised funds for Malaria | closed | 13/08/2026             | 15/08/2023      | 1200 FCFA   | Raised funds for malaria |

      And the deposit history details is also displayed with the following informations

        | Validate date | Person |
        | 10/10/2022    | Elie   |

    Scenario: account manager reactivated the deposit
      Given account manager selected one transaction from the list
      When account manager consults deposit's details
      Then the deposit details is displayed in descending order based on the validate's date with the following informations

        | Identifier  | Activated date | Name                     | Status | User's expiration date | Expiration date | Amount      | Description              |
        | 01-DEP-2022 | 12/08/2022     | Raised funds for Malaria | closed | 13/08/2026             | 15/08/2023      | 1200 FCFA   | Raised funds for malaria |

      And the deposit history details is also displayed with the following informations

        | Request date | Reactivate date | Validate date | Person |
        | 15/08/2022   | 29/09/2022      | 10/10/2022    | Elie   |

  Rule: Access list of transaction

    Scenario: account manager searches transactions for a specific range of dates
      Given account manager consulted the list of transaction
      When account manager searches all transactions created between 23/08/2023 - 30/08/2024
      Then the list is displayed in descending order based on the transaction's creation date
        | Identifier   | Date       |
        | Tx-<exflo>   | 21/08/2024 |

        | Identifier   | Date       |
        | Tx-<xloki>   | 23/07/2023 |

        | Identifier   | Date       |
        | Tx-<xloki>   | 23/05/2023 |

    Scenario: account manager searches transactions where deposits have not been reactivated
      Given account manager consulted the list of transaction
      When account manager searches all transactions where deposits have not been reactivated
      Then the list is displayed in descending order based on the transaction's creation date
        | Identifier   | Date       |
        | Tx-<exflo>   | 23/08/2023 |

        | Identifier   | Date       |
        | Tx-<xloki>   | 23/07/2023 |

    Scenario: account manager searches transactions that contains some characters
      Given account manager consulted the list of transactions
      When account manager searches all transactions that contain 'x'
      Then the list is displayed in descending order based on the transaction's creation date
        | Identifier   | Date       |
        | Tx-<exflo>   | 23/08/2023 |

        | Identifier   | Date       |
        | Tx-<xloki>   | 23/07/2023 |

    Scenario: account manager consults list of transaction
      Given account manager is successfully connected
      When account manager consults the list of transactions
      Then by default the transactions of the current year are displayed in descending order based on the transaction's creation date
        | Identifier       | Date       |
        | Tx-<username>    | 23/08/2023 |

        | Identifier       | Date       |
        | Tx-<username>    | 23/07/2023 |
