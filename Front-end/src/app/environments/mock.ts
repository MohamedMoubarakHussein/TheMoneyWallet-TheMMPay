import { User, UserWallet, Transaction, Notification } from '../entity/UnifiedResponse';

export const mockUsers: User[] = [
  {
    userId: 'user1',
    email: 'john.doe@example.com',
    firstName: 'John',
    lastName: 'Doe',
    fullName: 'John Doe',
    phone: '+1234567890',
    avatar: 'https://via.placeholder.com/150',
    status: 'active',
    emailVerified: true,
    phoneVerified: true,
    twoFactorEnabled: false,
    primaryWalletId: 'wallet1',
    totalBalance: 2500.75,
    walletsCount: 2,
    recentTransactionsCount: 15,
    wallets: [],
    recentTransactions: [],
    Notifications: [],
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-15'),
    lastLoginAt: new Date('2024-01-15'),
    contacts: [],
    paymentMethods: [],
    bankAccounts: [],
    securitySettings: {
      twoFactorEnabled: false,
      twoFactorMethod: 'sms',
      loginNotifications: true,
      transactionNotifications: true,
      sessionTimeout: 30,
      maxLoginAttempts: 5,
      requirePasswordChange: false,
      lastPasswordChange: new Date('2024-01-01')
    },
    preferences: {
      language: 'en',
      timezone: 'America/New_York',
      currency: 'USD',
      theme: 'light',
      notifications: {
        email: true,
        sms: true,
        push: true
      }
    }
  },
  {
    userId: 'user2',
    email: 'jane.smith@example.com',
    firstName: 'Jane',
    lastName: 'Smith',
    fullName: 'Jane Smith',
    phone: '+1987654321',
    avatar: 'https://via.placeholder.com/150',
    status: 'active',
    emailVerified: true,
    phoneVerified: false,
    twoFactorEnabled: true,
    primaryWalletId: 'wallet3',
    totalBalance: 1875.50,
    walletsCount: 1,
    recentTransactionsCount: 8,
    wallets: [],
    recentTransactions: [],
    Notifications: [],
    createdAt: new Date('2024-01-05'),
    updatedAt: new Date('2024-01-14'),
    lastLoginAt: new Date('2024-01-14'),
    contacts: [],
    paymentMethods: [],
    bankAccounts: [],
    securitySettings: {
      twoFactorEnabled: true,
      twoFactorMethod: 'authenticator',
      loginNotifications: true,
      transactionNotifications: true,
      sessionTimeout: 15,
      maxLoginAttempts: 3,
      requirePasswordChange: false,
      lastPasswordChange: new Date('2024-01-05')
    },
    preferences: {
      language: 'en',
      timezone: 'America/Los_Angeles',
      currency: 'USD',
      theme: 'dark',
      notifications: {
        email: true,
        sms: false,
        push: true
      }
    }
  }
];

export const mockWallets: UserWallet[] = [
  {
    id: 'wallet1',
    userId: 'user1',
    name: 'Main Checking',
    type: 'checking',
    balance: 1500.25,
    currency: 'USD',
    status: 'active',
    description: 'Primary checking account',
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-15'),
    isPrimary: true,
    lastTransactionAt: new Date('2024-01-15')
  },
  {
    id: 'wallet2',
    userId: 'user1',
    name: 'Savings',
    type: 'savings',
    balance: 1000.50,
    currency: 'USD',
    status: 'active',
    description: 'High-yield savings account',
    createdAt: new Date('2024-01-01'),
    updatedAt: new Date('2024-01-15'),
    isPrimary: false,
    lastTransactionAt: new Date('2024-01-10')
  },
  {
    id: 'wallet3',
    userId: 'user2',
    name: 'Business Account',
    type: 'business',
    balance: 1875.50,
    currency: 'USD',
    status: 'active',
    description: 'Business operations account',
    createdAt: new Date('2024-01-05'),
    updatedAt: new Date('2024-01-14'),
    isPrimary: true,
    lastTransactionAt: new Date('2024-01-14')
  }
];

export const mockTransactions: Transaction[] = [
  {
    id: 'tx1',
    walletId: 'wallet1',
    type: 'received',
    amount: 500.00,
    currency: 'USD',
    status: 'completed',
    description: 'Salary deposit',
    recipientId: 'user1',
    category: 'income',
    tags: ['salary', 'deposit'],
    metadata: { source: 'employer' },
    createdAt: new Date('2024-01-15'),
    updatedAt: new Date('2024-01-15'),
    completedAt: new Date('2024-01-15')
  },
  {
    id: 'tx2',
    walletId: 'wallet1',
    type: 'sent',
    amount: 75.50,
    currency: 'USD',
    status: 'completed',
    description: 'Grocery shopping',
    recipientId: 'store1',
    category: 'shopping',
    tags: ['groceries', 'food'],
    metadata: { store: 'SuperMarket' },
    createdAt: new Date('2024-01-14'),
    updatedAt: new Date('2024-01-14'),
    completedAt: new Date('2024-01-14')
  },
  {
    id: 'tx3',
    walletId: 'wallet1',
    type: 'transfer',
    amount: 200.00,
    currency: 'USD',
    status: 'completed',
    description: 'Transfer to savings',
    recipientId: 'wallet2',
    category: 'transfer',
    tags: ['internal', 'savings'],
    metadata: { purpose: 'savings' },
    createdAt: new Date('2024-01-13'),
    updatedAt: new Date('2024-01-13'),
    completedAt: new Date('2024-01-13')
  }
];

export const mockNotifications: Notification[] = [
  {
    id: 'notif1',
    userId: 'user1',
    title: 'Transaction Completed',
    message: 'Your transfer of $200.00 to savings has been completed successfully.',
    type: 'success',
    read: false,
    actionUrl: '/transactions/tx3',
    metadata: { transactionId: 'tx3' },
    createdAt: new Date('2024-01-13'),
    updatedAt: new Date('2024-01-13'),
    expiresAt: new Date('2024-01-20')
  },
  {
    id: 'notif2',
    userId: 'user1',
    title: 'New Login',
    message: 'New login detected from New York, NY. If this wasn\'t you, please contact support.',
    type: 'info',
    read: true,
    actionUrl: '/security',
    metadata: { location: 'New York, NY', device: 'Chrome on Windows' },
    createdAt: new Date('2024-01-12'),
    updatedAt: new Date('2024-01-12'),
    expiresAt: new Date('2024-01-19')
  }
];

export const mockUnifiedResponse = {
  data: {
    users: mockUsers,
    wallets: mockWallets,
    transactions: mockTransactions,
    notifications: mockNotifications
  },
  timeStamp: new Date().toISOString(),
  statusInternalCode: 'SUCCESS',
  haveData: true,
  haveError: false
};