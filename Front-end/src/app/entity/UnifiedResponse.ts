
export interface UnifiedResponse {
  data: { [key: string]: { [key: string]: string } };
  timeStamp: string; 
  statusInternalCode: string;
  haveData: boolean;
  haveError: boolean;
}

/*

export interface User {
  userId: string;
  fullName: string;
  email: string;
  totalBalance: string;
  primaryWalletId: string;
  lastUpdated: string;
  walletsCount:string;
  recentTransactionsCount:string;
  wallets:UserWallet[];
  recentTransactions:Transaction[];
  Notifications:Notification[];
  noneReadNotificationsCounter:String;

}

export interface SignupData {
  userName: string;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword: string;
  terms: boolean;
}

export interface User{}



export interface Transaction {
  id: string;
  amount: number;
  name: string;
  date: string;
  type: 'shopping' | 'payment' | 'food';
}

export interface UserWallet{
   id:string;
   balance:number;
   currency:string;
   name:string;
   status:string;
}



export interface Notification{
  id:string;
  title:string;
  message:string;
  read:boolean;
  createdDate:Date;
  notificationType:string;

}
*/

export interface SignupData {
  userName: string;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  confirmPassword: string;
  terms: boolean;
}

// Add these to your entity/UnifiedResponse.ts file

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  error?: string;
  timestamp?: string;
}

export interface Transaction {
  id: string;
  walletId: string;
  type: 'sent' | 'received' | 'deposit' | 'withdrawal' | 'transfer' | 'payment' | 'refund';
  amount: number;
  currency: string;
  status: 'pending' | 'completed' | 'failed' | 'cancelled' | 'refunded';
  description?: string;
  recipientId?: string;
  recipientEmail?: string;
  recipientPhone?: string;
  senderId?: string;
  category?: string;
  tags?: string[];
  metadata?: Record<string, any>;
  createdAt: Date;
  updatedAt: Date;
  completedAt?: Date;
  failureReason?: string;
}

export interface Notification {
  id: string;
  userId: string;
  title: string;
  message: string;
  type: 'info' | 'success' | 'warning' | 'error';
  read: boolean;
  actionUrl?: string;
  metadata?: Record<string, any>;
  createdAt: Date;
  updatedAt: Date;
  expiresAt?: Date;
}

export interface UserWallet {
  id: string;
  userId: string;
  name: string;
  type: 'checking' | 'savings' | 'credit' | 'crypto' | 'business' | 'investment';
  balance: number;
  currency: string;
  status: 'active' | 'inactive' | 'suspended' | 'closed';
  description?: string;
  createdAt: Date;
  updatedAt: Date;
  isPrimary: boolean;
  lastTransactionAt?: Date;
}

export interface User {
  userId: string;
  email: string;
  firstName?: string;
  lastName?: string;
  fullName?: string;
  phone?: string;
  avatar?: string;
  status: 'active' | 'inactive' | 'suspended';
  emailVerified: boolean;
  phoneVerified: boolean;
  twoFactorEnabled: boolean;
  primaryWalletId?: string;
  totalBalance: number;
  walletsCount: number;
  recentTransactionsCount: number;
  wallets: UserWallet[];
  recentTransactions: Transaction[];
  Notifications: Notification[];
  createdAt: Date;
  updatedAt: Date;
  lastLoginAt?: Date;
}



// Interface for API responses
export interface VerificationResponse {
  success: boolean;
  message: string;
  data?: any;
}

export interface ResendCodeResponse {
  success: boolean;
  message: string;
  cooldownTime?: number;
}


export interface WalletType {
  id: string;
  name: string;
  description: string;
  icon: string;
  gradient: string;
  features: string[];
}

export const CURRENTWALLETTYPES: WalletType[] = [
    {
      id: 'personal',
      name: 'Personal Wallet',
      description: 'Perfect for everyday spending and personal finance management',
      icon: 'fas fa-wallet',
      gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      features: ['Daily transactions', 'Budget tracking', 'Expense categorization']
    },
    {
      id: 'business',
      name: 'Business Wallet',
      description: 'Designed for business operations and professional transactions',
      icon: 'fas fa-building',
      gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
      features: ['Invoice management', 'Tax tracking', 'Business analytics']
    },
    {
      id: 'savings',
      name: 'Savings Wallet',
      description: 'Built for long-term savings goals and investment tracking',
      icon: 'fas fa-piggy-bank',
      gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
      features: ['Goal setting', 'Interest tracking', 'Savings milestones']
    }
  ];



   export const CURRENCIES: string[] = ['USD', 'EUR', 'GBP', 'CAD', 'AUD', 'JPY', 'CHF'];


   export interface CreateWalletRequest {
  name: string;
  walletType: 'Personal' | 'Business' | 'Savings' ;
  currency: string;
  description?: string;
}

export interface UpdateWalletRequest {
  name?: string;
  description?: string;
  currency?: string;
}

export interface TransferRequest {
  fromWalletId: string;
  toWalletId: string;
  amount: number;
  description?: string;
}

export interface Invoice {
  id: string;
  userId: string;
  walletId: string;
  invoiceNumber: string;
  amount: number;
  currency: string;
  status: 'draft' | 'sent' | 'paid' | 'overdue' | 'cancelled';
  dueDate: Date;
  issueDate: Date;
  paidDate?: Date;
  recipient: {
    name: string;
    email: string;
    phone?: string;
    address?: string;
  };
  items: InvoiceItem[];
  notes?: string;
  terms?: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface InvoiceItem {
  id: string;
  description: string;
  quantity: number;
  unitPrice: number;
  total: number;
}

export interface Budget {
  id: string;
  userId: string;
  walletId: string;
  name: string;
  amount: number;
  currency: string;
  period: 'daily' | 'weekly' | 'monthly' | 'yearly';
  category: string;
  spent: number;
  remaining: number;
  startDate: Date;
  endDate: Date;
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
}

export interface RecurringPayment {
  id: string;
  userId: string;
  walletId: string;
  name: string;
  amount: number;
  currency: string;
  frequency: 'daily' | 'weekly' | 'monthly' | 'yearly';
  nextPaymentDate: Date;
  endDate?: Date;
  recipient: {
    name: string;
    email?: string;
    phone?: string;
    accountNumber?: string;
  };
  description?: string;
  isActive: boolean;
  createdAt: Date;
  updatedAt: Date;
}

export interface CurrencyConversion {
  fromCurrency: string;
  toCurrency: string;
  rate: number;
  lastUpdated: Date;
}

export interface SecuritySettings {
  twoFactorEnabled: boolean;
  twoFactorMethod: 'sms' | 'email' | 'authenticator';
  loginNotifications: boolean;
  transactionNotifications: boolean;
  sessionTimeout: number; // in minutes
  maxLoginAttempts: number;
  requirePasswordChange: boolean;
  lastPasswordChange: Date;
  recentLogins: Array<{
    browser: string;
    os: string;
    deviceType: 'mobile' | 'tablet' | 'desktop';
    location: string;
    timestamp: Date;
    successful: boolean;
  }>;
  connectedApps: Array<{
    name: string;
    icon: string;
    accessLevel: string;
    connectedDate: Date;
  }>;
}

export interface AuditLog {
  id: string;
  userId: string;
  action: string;
  resource: string;
  resourceId?: string;
  details: Record<string, any>;
  ipAddress: string;
  userAgent: string;
  timestamp: Date;
}

export interface Contact {
  id: string;
  userId: string;
  name: string;
  email?: string;
  phone?: string;
  avatar?: string;
  isFavorite: boolean;
  lastTransactionDate?: Date;
  totalTransactions: number;
  createdAt: Date;
  updatedAt: Date;
}

export interface PaymentMethod {
  id: string;
  userId: string;
  type: 'card' | 'bank' | 'wallet';
  name: string;
  last4?: string;
  expiryDate?: string;
  isDefault: boolean;
  isActive: boolean;
  metadata: Record<string, any>;
  createdAt: Date;
  updatedAt: Date;
}

export interface BankAccount {
  id: string;
  userId: string;
  bankName: string;
  accountNumber: string;
  routingNumber: string;
  accountType: 'checking' | 'savings';
  isVerified: boolean;
  isDefault: boolean;
  createdAt: Date;
  updatedAt: Date;
}

// Enhanced User interface with new properties
export interface User {
  userId: string;
  email: string;
  firstName?: string;
  lastName?: string;
  fullName?: string;
  phone?: string;
  avatar?: string;
  status: 'active' | 'inactive' | 'suspended';
  emailVerified: boolean;
  phoneVerified: boolean;
  twoFactorEnabled: boolean;
  primaryWalletId?: string;
  totalBalance: number;
  walletsCount: number;
  recentTransactionsCount: number;
  wallets: UserWallet[];
  recentTransactions: Transaction[];
  Notifications: Notification[];
  contacts: Contact[];
  paymentMethods: PaymentMethod[];
  bankAccounts: BankAccount[];
  securitySettings: SecuritySettings;
  createdAt: Date;
  updatedAt: Date;
  lastLoginAt?: Date;
  preferences: {
    language: string;
    timezone: string;
    currency: string;
    theme: 'light' | 'dark' | 'auto';
    notifications: {
      email: boolean;
      sms: boolean;
      push: boolean;
    };
  };
}