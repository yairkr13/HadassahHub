import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { Button, Card, Input } from '@/components/ui';
import { useAuth } from '@/hooks/useAuth';
import { ROUTES } from '@/router/routes';
import { RegisterRequest } from '@/types/auth.types';
import { ALLOWED_EMAIL_DOMAINS } from '@/utils/constants';

export const RegisterPage: React.FC = () => {
  const [formData, setFormData] = useState<RegisterRequest>({
    displayName: '',
    email: '',
    password: '',
  });
  const [errors, setErrors] = useState<Partial<Record<keyof (RegisterRequest & { confirmPassword: string }), string>>>({});
  const [confirmPassword, setConfirmPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [touched, setTouched] = useState<Partial<Record<keyof (RegisterRequest & { confirmPassword: string }), boolean>>>({});

  const { register, authError, clearAuthError } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  // Get the page user was trying to access before being redirected to register
  const from = location.state?.from?.pathname || ROUTES.COURSES;

  // Clear auth error when component mounts or form data changes
  useEffect(() => {
    if (authError) {
      clearAuthError();
    }
  }, [formData, confirmPassword, authError, clearAuthError]);

  const validateField = (name: string, value: string): string | undefined => {
    switch (name) {
      case 'displayName':
        if (!value) return 'Display name is required';
        if (value.length < 2) return 'Display name must be at least 2 characters';
        if (value.length > 50) return 'Display name must be less than 50 characters';
        return undefined;
      case 'email':
        if (!value) return 'Email is required';
        if (!/\S+@\S+\.\S+/.test(value)) return 'Please enter a valid email address';
        const isValidDomain = ALLOWED_EMAIL_DOMAINS.some(domain => value.endsWith(domain));
        if (!isValidDomain) {
          return 'Please use your academic email (@edu.hac.ac.il or @edu.jmc.ac.il)';
        }
        return undefined;
      case 'password':
        if (!value) return 'Password is required';
        if (value.length < 6) return 'Password must be at least 6 characters';
        if (!/(?=.*[a-z])(?=.*[A-Z])/.test(value)) return 'Password must contain both uppercase and lowercase letters';
        return undefined;
      case 'confirmPassword':
        if (!value) return 'Please confirm your password';
        if (value !== formData.password) return 'Passwords do not match';
        return undefined;
      default:
        return undefined;
    }
  };

  const validateForm = (): boolean => {
    const newErrors: Partial<Record<keyof (RegisterRequest & { confirmPassword: string }), string>> = {};
    
    // Validate all form fields
    Object.keys(formData).forEach((key) => {
      const fieldName = key as keyof RegisterRequest;
      const error = validateField(fieldName, formData[fieldName]);
      if (error) {
        newErrors[fieldName] = error;
      }
    });

    // Validate confirm password
    const confirmPasswordError = validateField('confirmPassword', confirmPassword);
    if (confirmPasswordError) {
      newErrors.confirmPassword = confirmPasswordError;
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    // Mark all fields as touched
    setTouched({ 
      displayName: true, 
      email: true, 
      password: true, 
      confirmPassword: true 
    });
    
    if (!validateForm()) return;

    setIsLoading(true);
    try {
      await register(formData);
      // Redirect to the page user was trying to access, or courses by default
      navigate(from, { replace: true });
    } catch (error: any) {
      // Error handling is now managed by AuthContext
      console.error('Registration error:', error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    
    if (name === 'confirmPassword') {
      setConfirmPassword(value);
      // Real-time validation for confirm password if touched
      if (touched.confirmPassword) {
        const fieldError = validateField('confirmPassword', value);
        setErrors(prev => ({ ...prev, confirmPassword: fieldError }));
      }
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
      // Real-time validation for touched fields
      if (touched[name as keyof RegisterRequest]) {
        const fieldError = validateField(name, value);
        setErrors(prev => ({ ...prev, [name]: fieldError }));
      }
    }
  };

  const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    
    // Mark field as touched and validate
    if (name === 'confirmPassword') {
      setTouched(prev => ({ ...prev, confirmPassword: true }));
      const fieldError = validateField('confirmPassword', value);
      setErrors(prev => ({ ...prev, confirmPassword: fieldError }));
    } else {
      const fieldName = name as keyof RegisterRequest;
      setTouched(prev => ({ ...prev, [fieldName]: true }));
      const fieldError = validateField(fieldName, value);
      setErrors(prev => ({ ...prev, [fieldName]: fieldError }));
    }
  };

  return (
    <div className="min-h-screen bg-background-soft flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-text-primary">Register</h1>
          <p className="mt-2 text-text-secondary">
            Join the HadassahHub community
          </p>
        </div>

        <Card>
          {authError && (
            <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
              <div className="flex items-center">
                <svg className="w-5 h-5 text-red-600 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.732 16.5c-.77.833.192 2.5 1.732 2.5z" />
                </svg>
                <p className="text-red-800 text-sm">{authError}</p>
              </div>
            </div>
          )}
          
          <form onSubmit={handleSubmit} className="space-y-6">
            <Input
              label="Display Name"
              type="text"
              name="displayName"
              value={formData.displayName}
              onChange={handleChange}
              onBlur={handleBlur}
              error={touched.displayName ? errors.displayName : undefined}
              placeholder="Your full name"
              required
            />

            <Input
              label="Academic Email"
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              onBlur={handleBlur}
              error={touched.email ? errors.email : undefined}
              placeholder="your.email@edu.hac.ac.il"
              required
            />

            <Input
              label="Password"
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              onBlur={handleBlur}
              error={touched.password ? errors.password : undefined}
              placeholder="Choose a strong password (6+ chars, mixed case)"
              required
            />

            <Input
              label="Confirm Password"
              type="password"
              name="confirmPassword"
              value={confirmPassword}
              onChange={handleChange}
              onBlur={handleBlur}
              error={touched.confirmPassword ? errors.confirmPassword : undefined}
              placeholder="Confirm your password"
              required
            />

            <Button
              type="submit"
              variant="primary"
              className="w-full"
              loading={isLoading}
              disabled={Object.keys(errors).some(key => errors[key as keyof (RegisterRequest & { confirmPassword: string })])}
            >
              Register
            </Button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-text-secondary">
              Already have an account?{' '}
              <Link
                to={ROUTES.LOGIN}
                className="text-primary hover:text-primary/80 font-medium"
              >
                Sign in here
              </Link>
            </p>
          </div>
        </Card>
      </div>
    </div>
  );
};