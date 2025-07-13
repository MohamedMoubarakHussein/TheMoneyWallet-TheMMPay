import { User, Transaction, Notification, UserWallet, UnifiedResponse } from '../entity/UnifiedResponse';

// Mock Users
export const mockUsers: User[] = [
  {
    userId: '1',
    email: 'john.doe@example.com',
    firstName: 'John',
    lastName: 'Doe',
    fullName: 'John Doe',
    phone: '+1234567890',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=John',
    status: 'active',
    emailVerified: true,
    phoneVerified: true,
    twoFactorEnabled: false,
    primaryWalletId: 'wallet-1',
    totalBalance: 12450.75,
    walletsCount: 3,
    recentTransactionsCount: 8,
    wallets: [], // Will be populated below
    recentTransactions: [], // Will be populated below
    Notifications: [], // Will be populated below
    createdAt: new Date('2023-01-15'),
    updatedAt: new Date('2024-06-27'),
    lastLoginAt: new Date('2024-06-27T10:30:00Z')
  },
  {
    userId: '2',
    email: 'jane.smith@example.com',
    firstName: 'Jane',
    lastName: 'Smith',
    fullName: 'Jane Smith',
    phone: '+1987654321',
    status: 'active',
    emailVerified: true,
    phoneVerified: false,
    twoFactorEnabled: true,
    primaryWalletId: 'wallet-4',
    totalBalance: 8750.25,
    walletsCount: 2,
    recentTransactionsCount: 5,
    wallets: [],
    recentTransactions: [],
    Notifications: [],
    createdAt: new Date('2023-03-20'),
    updatedAt: new Date('2024-06-26'),
    lastLoginAt: new Date('2024-06-26T15:45:00Z')
  }
];

// Mock Wallets
export const mockWallets: UserWallet[] = [
  {
    id: 'wallet-1',
    userId: '1',
    name: 'Primary Checking',
    type: 'checking',
    balance: 5420.50,
    currency: 'USD',
    status: 'active',
    description: 'Main checking account for daily expenses',
    createdAt: new Date('2023-01-15'),
    updatedAt: new Date('2024-06-27'),
    isPrimary: false,
    lastTransactionAt: new Date('2024-06-27T09:15:00Z')
  },
  {
    id: 'wallet-2',
    userId: '1',
    name: 'Savings Goal',
    type: 'savings',
    balance: 6830.25,
    currency: 'USD',
    status: 'active',
    description: 'Emergency fund and vacation savings',
    createdAt: new Date('2023-02-01'),
    updatedAt: new Date('2024-06-26'),
    isPrimary: false,
    lastTransactionAt: new Date('2024-06-25T14:20:00Z')
  },
  {
    id: 'wallet-3',
    userId: '1',
    name: 'Travel Card',
    type: 'credit',
    balance: 200.00,
    currency: 'USD',
    status: 'active',
    description: 'Credit card for travel expenses',
    createdAt: new Date('2023-04-10'),
    updatedAt: new Date('2024-06-24'),
    isPrimary: false,
    lastTransactionAt: new Date('2024-06-24T18:30:00Z')
  },
  {
    id: 'wallet-4',
    userId: '2',
    name: 'Business Account',
    type: 'business',
    balance: 7200.00,
    currency: 'USD',
    status: 'active',
    description: 'Business checking account',
    createdAt: new Date('2023-03-20'),
    updatedAt: new Date('2024-06-26'),
    isPrimary: false,
    lastTransactionAt: new Date('2024-06-26T11:45:00Z')
  },
  {
    id: 'wallet-5',
    userId: '2',
    name: 'Crypto Portfolio',
    type: 'crypto',
    balance: 1550.25,
    currency: 'USD',
    status: 'active',
    description: 'Bitcoin and Ethereum holdings',
    createdAt: new Date('2023-05-15'),
    updatedAt: new Date('2024-06-25'),
    isPrimary: false,
    lastTransactionAt: new Date('2024-06-25T16:20:00Z')
  },
  {
    id: 'wallet-6',
    userId: '1',
    name: 'Old Account',
    type: 'checking',
    balance: 0.00,
    currency: 'USD',
    status: 'inactive',
    description: 'Closed checking account',
    createdAt: new Date('2022-01-01'),
    updatedAt: new Date('2023-12-01'),
    isPrimary: false,
    lastTransactionAt: new Date('2023-11-30T10:00:00Z')
  }
];

// Mock Transactions
export const mockTransactions: Transaction[] = [
  {
    id: 'txn-1',
    walletId: 'wallet-1',
    type: 'received',
    amount: 1250.00,
    currency: 'USD',
    status: 'completed',
    description: 'Salary deposit',
    senderId: 'employer-123',
    category: 'income',
    tags: ['salary', 'monthly'],
    createdAt: new Date('2024-06-27T08:00:00Z'),
    updatedAt: new Date('2024-06-27T08:01:00Z'),
    completedAt: new Date('2024-06-27T08:01:00Z')
  },
  {
    id: 'txn-2',
    walletId: 'wallet-1',
    type: 'sent',
    amount: 85.50,
    currency: 'USD',
    status: 'completed',
    description: 'Grocery shopping',
    recipientEmail: 'payments@supermarket.com',
    category: 'food',
    tags: ['groceries', 'food'],
    createdAt: new Date('2024-06-26T18:30:00Z'),
    updatedAt: new Date('2024-06-26T18:31:00Z'),
    completedAt: new Date('2024-06-26T18:31:00Z')
  },
  {
    id: 'txn-3',
    walletId: 'wallet-1',
    type: 'sent',
    amount: 45.00,
    currency: 'USD',
    status: 'completed',
    description: 'Gas station payment',
    recipientEmail: 'billing@gasstation.com',
    category: 'transport',
    tags: ['fuel', 'car'],
    createdAt: new Date('2024-06-26T15:20:00Z'),
    updatedAt: new Date('2024-06-26T15:21:00Z'),
    completedAt: new Date('2024-06-26T15:21:00Z')
  },
  {
    id: 'txn-4',
    walletId: 'wallet-2',
    type: 'deposit',
    amount: 500.00,
    currency: 'USD',
    status: 'completed',
    description: 'Monthly savings transfer',
    category: 'savings',
    tags: ['savings', 'automatic'],
    createdAt: new Date('2024-06-25T12:00:00Z'),
    updatedAt: new Date('2024-06-25T12:01:00Z'),
    completedAt: new Date('2024-06-25T12:01:00Z')
  },
  {
    id: 'txn-5',
    walletId: 'wallet-1',
    type: 'payment',
    amount: 1200.00,
    currency: 'USD',
    status: 'completed',
    description: 'Monthly rent payment',
    recipientEmail: 'rent@apartments.com',
    category: 'housing',
    tags: ['rent', 'monthly'],
    createdAt: new Date('2024-06-25T09:00:00Z'),
    updatedAt: new Date('2024-06-25T09:01:00Z'),
    completedAt: new Date('2024-06-25T09:01:00Z')
  },
  {
    id: 'txn-6',
    walletId: 'wallet-3',
    type: 'payment',
    amount: 150.00,
    currency: 'USD',
    status: 'pending',
    description: 'Hotel booking',
    recipientEmail: 'reservations@hotel.com',
    category: 'travel',
    tags: ['hotel', 'vacation'],
    createdAt: new Date('2024-06-24T20:15:00Z'),
    updatedAt: new Date('2024-06-24T20:15:00Z')
  },
  {
    id: 'txn-7',
    walletId: 'wallet-1',
    type: 'received',
    amount: 75.00,
    currency: 'USD',
    status: 'completed',
    description: 'Refund from online store',
    senderId: 'store-456',
    category: 'refund',
    tags: ['refund', 'shopping'],
    createdAt: new Date('2024-06-24T16:45:00Z'),
    updatedAt: new Date('2024-06-24T16:46:00Z'),
    completedAt: new Date('2024-06-24T16:46:00Z')
  },
  {
    id: 'txn-8',
    walletId: 'wallet-4',
    type: 'received',
    amount: 2500.00,
    currency: 'USD',
    status: 'completed',
    description: 'Client payment',
    senderId: 'client-789',
    category: 'business',
    tags: ['invoice', 'client'],
    createdAt: new Date('2024-06-24T14:30:00Z'),
    updatedAt: new Date('2024-06-24T14:31:00Z'),
    completedAt: new Date('2024-06-24T14:31:00Z')
  },
  {
    id: 'txn-9',
    walletId: 'wallet-1',
    type: 'transfer',
    amount: 300.00,
    currency: 'USD',
    status: 'failed',
    description: 'Transfer to savings',
    failureReason: 'Insufficient funds',
    category: 'transfer',
    tags: ['savings', 'transfer'],
    createdAt: new Date('2024-06-23T10:00:00Z'),
    updatedAt: new Date('2024-06-23T10:05:00Z')
  },
  {
    id: 'txn-10',
    walletId: 'wallet-5',
    type: 'deposit',
    amount: 500.00,
    currency: 'USD',
    status: 'completed',
    description: 'Bitcoin purchase',
    category: 'investment',
    tags: ['crypto', 'bitcoin'],
    createdAt: new Date('2024-06-22T16:20:00Z'),
    updatedAt: new Date('2024-06-22T16:25:00Z'),
    completedAt: new Date('2024-06-22T16:25:00Z')
  }
];

// Mock Notifications
export const mockNotifications: Notification[] = [
  {
    id: 'notif-1',
    userId: '1',
    title: 'Payment Received',
    message: 'Your salary of $1,250.00 has been deposited successfully.',
    type: 'success',
    read: false,
    actionUrl: '/transactions/txn-1',
    metadata: { transactionId: 'txn-1', amount: 1250.00 },
    createdAt: new Date('2024-06-27T08:01:00Z'),
    updatedAt: new Date('2024-06-27T08:01:00Z')
  },
  {
    id: 'notif-2',
    userId: '1',
    title: 'Low Balance Alert',
    message: 'Your Travel Card balance is below $500. Consider adding funds.',
    type: 'warning',
    read: false,
    actionUrl: '/wallets/wallet-3',
    metadata: { walletId: 'wallet-3', balance: 200.00 },
    createdAt: new Date('2024-06-26T19:00:00Z'),
    updatedAt: new Date('2024-06-26T19:00:00Z')
  },
  {
    id: 'notif-3',
    userId: '1',
    title: 'Transaction Failed',
    message: 'Transfer of $300.00 to savings failed due to insufficient funds.',
    type: 'error',
    read: true,
    actionUrl: '/transactions/txn-9',
    metadata: { transactionId: 'txn-9', amount: 300.00 },
    createdAt: new Date('2024-06-23T10:05:00Z'),
    updatedAt: new Date('2024-06-23T11:00:00Z')
  },
  {
    id: 'notif-4',
    userId: '1',
    title: 'Refund Processed',
    message: 'Refund of $75.00 from online store has been credited to your account.',
    type: 'info',
    read: true,
    actionUrl: '/transactions/txn-7',
    metadata: { transactionId: 'txn-7', amount: 75.00 },
    createdAt: new Date('2024-06-24T16:46:00Z'),
    updatedAt: new Date('2024-06-24T17:30:00Z')
  },
  {
    id: 'notif-5',
    userId: '1',
    title: 'Security Alert',
    message: 'New login detected from Chrome on Windows. If this wasn\'t you, please secure your account.',
    type: 'warning',
    read: false,
    actionUrl: '/security',
    metadata: { loginTime: '2024-06-27T10:30:00Z', device: 'Chrome on Windows' },
    createdAt: new Date('2024-06-27T10:31:00Z'),
    updatedAt: new Date('2024-06-27T10:31:00Z')
  },
  {
    id: 'notif-6',
    userId: '2',
    title: 'Large Transaction Alert',
    message: 'Client payment of $2,500.00 received in your business account.',
    type: 'info',
    read: true,
    actionUrl: '/transactions/txn-8',
    metadata: { transactionId: 'txn-8', amount: 2500.00 },
    createdAt: new Date('2024-06-24T14:31:00Z'),
    updatedAt: new Date('2024-06-24T15:00:00Z')
  },
  {
    id: 'notif-7',
    userId: '1',
    title: 'Monthly Savings Goal',
    message: 'Great job! You\'ve saved $500 this month. Keep it up!',
    type: 'success',
    read: false,
    actionUrl: '/goals',
    metadata: { savedAmount: 500.00, goalAmount: 1000.00 },
    createdAt: new Date('2024-06-25T12:01:00Z'),
    updatedAt: new Date('2024-06-25T12:01:00Z')
  }
];

// Assign relationships
mockUsers[0].wallets = mockWallets.filter(w => w.userId === '1');
mockUsers[0].recentTransactions = mockTransactions.filter(t => 
  mockUsers[0].wallets.some(w => w.id === t.walletId)
).slice(0, 5);
mockUsers[0].Notifications = mockNotifications.filter(n => n.userId === '1');

mockUsers[1].wallets = mockWallets.filter(w => w.userId === '2');
mockUsers[1].recentTransactions = mockTransactions.filter(t => 
  mockUsers[1].wallets.some(w => w.id === t.walletId)
).slice(0, 3);
mockUsers[1].Notifications = mockNotifications.filter(n => n.userId === '2');

// Mock API Responses
export const mockUnifiedResponse: UnifiedResponse = {
  data: {
    user: {
      id: '1',
      email: 'john.doe@example.com',
      fullName: 'John Doe'
    }
  },
  timeStamp: new Date().toISOString(),
  statusInternalCode: 'SUCCESS_200',
  haveData: true,
  haveError: false
};

// Helper functions for generating dynamic mock data
export class MockDataGenerator {
  
  static generateTransaction(walletId: string, type: Transaction['type'] = 'sent'): Transaction {
    const id = `txn-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
    const amounts = [25.50, 45.00, 78.25, 120.00, 250.75, 500.00, 1000.00];
    const descriptions = {
      sent: ['Coffee shop', 'Grocery store', 'Gas station', 'Online shopping', 'Restaurant'],
      received: ['Salary deposit', 'Refund', 'Payment received', 'Cash back', 'Gift'],
      deposit: ['Bank transfer', 'Cash deposit', 'Check deposit'],
      payment: ['Bill payment', 'Subscription', 'Insurance', 'Utilities'],
      transfer: ['Savings transfer', 'Account transfer']
    };

    return {
      id,
      walletId,
      type,
      amount: amounts[Math.floor(Math.random() * amounts.length)],
      currency: 'USD',
      status: Math.random() > 0.1 ? 'completed' : 'pending',
      description: "ddd",
      createdAt: new Date(Date.now() - Math.random() * 7 * 24 * 60 * 60 * 1000),
      updatedAt: new Date()
    };
  }

  static generateNotification(userId: string): Notification {
    const id = `notif-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
    const types: Notification['type'][] = ['info', 'success', 'warning', 'error'];
    const titles = [
      'Payment Received',
      'Transaction Completed',
      'Security Alert',
      'Account Update',
      'Balance Alert'
    ];
    const messages = [
      'Your recent transaction has been processed successfully.',
      'A new payment has been received in your account.',
      'Please verify your recent login activity.',
      'Your account information has been updated.',
      'Your account balance is running low.'
    ];

    return {
      id,
      userId,
      title: titles[Math.floor(Math.random() * titles.length)],
      message: messages[Math.floor(Math.random() * messages.length)],
      type: types[Math.floor(Math.random() * types.length)],
      read: Math.random() > 0.3,
      createdAt: new Date(Date.now() - Math.random() * 24 * 60 * 60 * 1000),
      updatedAt: new Date()
    };
  }

  static generateWallet(userId: string, type: UserWallet['type'] = 'checking'): UserWallet {
    const id = `wallet-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
    const names = {
      checking: ['Primary Checking', 'Daily Expenses', 'Main Account'],
      savings: ['Emergency Fund', 'Vacation Savings', 'Future Goals'],
      credit: ['Travel Card', 'Shopping Card', 'Rewards Card'],
      crypto: ['Bitcoin Wallet', 'Crypto Portfolio', 'Digital Assets'],
      business: ['Business Account', 'Company Funds', 'Operations'],
      investment: ['Stock Portfolio', 'Investment Account', 'Trading Fund']
    };

    return {
      id,
      userId,
      name: names[type][Math.floor(Math.random() * names[type].length)],
      type,
      balance: Math.random() * 10000,
      currency: 'USD',
      status: Math.random() > 0.1 ? 'active' : 'inactive',
       isPrimary: false,
      createdAt: new Date(Date.now() - Math.random() * 365 * 24 * 60 * 60 * 1000),
      updatedAt: new Date()
    };
  }
}

// Export default user for quick testing
export const defaultMockUser = mockUsers[0];
export const defaultMockTransactions = mockTransactions.slice(0, 5);
export const defaultMockNotifications = mockNotifications.filter(n => n.userId === '1');
export const defaultMockWallets = mockWallets.filter(w => w.userId === '1');